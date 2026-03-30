# ADR-004: Reactive Stack (WebFlux + R2DBC) for the Promoter Module

| Campo        | Valor                                   |
|--------------|-----------------------------------------|
| **Estado**   | Aceptado                                |
| **Fecha**    | 2026-03-30                              |
| **Autores**  | Equipo GresK Backend                   |
| **Afecta a** | Módulo `promoter` — todas las capas     |

---

## 1. Contexto

GresK es una plataforma de gestión de eventos musicales. El módulo `promoter`
es el primer módulo de dominio implementado y establece el **patrón arquitectónico
de referencia** para el resto del proyecto.

Al diseñar este módulo se tomó la decisión de construir el backend entero con
**Spring WebFlux** (en lugar de Spring MVC) y **Spring Data R2DBC** (en lugar
de Spring Data JPA). Esta decisión tiene implicaciones profundas en el modelo de
programación, el diseño de los ports, la infraestructura de persistencia y los
patrones de test. Este documento recoge los motivos, las consecuencias y los
patrones adoptados para gestionar las limitaciones de este stack frente al stack
servlet clásico.

### Stack de referencia previo (descartado)

```
Spring MVC (Tomcat) + Spring Data JPA (Hibernate) + JDBC blocking
```

### Stack adoptado

```
Spring WebFlux (Netty) + Spring Data R2DBC + Project Reactor (Mono / Flux)
```

---

## 2. Decisión

**Usar Spring WebFlux y Spring Data R2DBC en todos los módulos del backend.**

La capa de dominio permanece puramente orientada a objetos y sin dependencias
reactivas. Sin embargo, **los ports del application layer exponen `Mono<T>` y
`Flux<T>`** como contratos reactivos que los adaptadores de infraestructura
implementan.

---

## 3. Motivos

### 3.1 Modelo de concurrencia no bloqueante

Spring MVC asigna **un hilo del thread pool por cada petición** mientras la
petición está viva (modelo "thread-per-request"). En operaciones que involucran
I/O (consultas a base de datos, llamadas HTTP externas), ese hilo queda
bloqueado esperando la respuesta.

Spring WebFlux usa **Netty con un bucle de eventos** (event loop). Un hilo del
event loop gestiona miles de peticiones simultáneas porque nunca se bloquea: en
cuanto una operación I/O necesita esperar, el hilo se libera y atiende otra
petición. Cuando llega el dato, la ejecución se reanuda en el mismo thread pool.

Para una plataforma de eventos con picos de carga (venta de entradas, apertura
de inscripciones), este modelo escala horizontalmente con muchos menos recursos.

### 3.2 Consistencia reactiva de extremo a extremo

Mezclar WebFlux con JDBC bloqueante es posible técnicamente
(usando `Schedulers.boundedElastic()`) pero introduce fricciones:

- Requiere gestionar explícitamente qué scheduler usa cada llamada a BD.
- Los stack traces son difíciles de seguir.
- Las transacciones JDBC usan ThreadLocals, que son incompatibles con el modelo
  reactivo (un Mono puede continuar en un hilo diferente al que lo inició).

R2DBC elimina estas fricciones porque **toda la I/O con la base de datos es
reactiva desde el driver**: el event loop nunca se bloquea esperando PostgreSQL.

### 3.3 Spring Boot 4 / Spring Framework 7

El proyecto usa Spring Boot 4.x (Spring Framework 7). En esta versión, la hoja
de ruta oficial de Spring prioriza el stack reactivo. Algunos test slices de
servlet (`@WebMvcTest`) siguen existiendo, pero `@WebFluxTest` fue eliminado
del módulo `spring-boot-test-autoconfigure` en favor de `WebTestClient` directo
(ver sección 6).

---

## 4. Por qué `Mono<T>` / `Flux<T>` en los ports de dominio es aceptable

### El argumento en contra

Un argumento habitual contra este diseño es que exponer `Mono`/`Flux` en los
ports del application layer introduce una dependencia del dominio en el modelo
de programación reactivo (Project Reactor), lo que viola la inversión de
dependencias: el dominio debería depender de abstracciones, no de una librería
concreta de async I/O.

### Por qué lo aceptamos en GresK

1. **Los ports están en la capa de aplicación, no en la de dominio.**
   El modelo de dominio (`Promoter`, `PromoterId`, `Email`, etc.) es 100%
   POJO sin ninguna dependencia reactiva. La reactividad empieza en los ports
   (`PromoterRepository`) que viven en `application/port` y son interfaces puras.

2. **`Mono`/`Flux` son contratos de asincronía, no de infraestructura.**
   Un `Mono<Promoter>` dice "te daré un Promoter en algún momento (o un error)".
   Eso es una abstracción válida para un port. El adaptador decide *cómo* cumplirla
   (R2DBC, HTTP, cache, mock...).

3. **La alternativa tiene un coste mayor.**
   Usar `CompletableFuture<T>` o callbacks en los ports y traducir a `Mono`
   en los adaptadores añade boilerplate sin ningún beneficio real para este
   proyecto. Los use cases ya usan operadores reactivos; los ports con `Mono`
   se integran de forma natural.

4. **Precedente en la comunidad.**
   Proyectos de referencia de arquitectura hexagonal reactiva (Axon, jhipster
   reactive) y la documentación oficial de Spring adoptan `Mono`/`Flux` en los
   ports cuando el stack es reactivo.

### Regla de oro

> El modelo de dominio nunca toca `Mono`, `Flux` ni ninguna API de Project Reactor.
> La reactividad empieza y termina en la capa de aplicación hacia arriba.

---

## 5. Patrón de la tabla de unión de géneros y por qué R2DBC requiere gestión manual de colecciones

### El problema

JPA con Hibernate gestiona automáticamente las relaciones `@OneToMany`,
`@ManyToMany` y sus tablas de unión. Cuando se llama a `save(promoter)`, Hibernate
inspecciona el grafo de objetos, detecta cambios en la colección y emite los
INSERT/DELETE necesarios en `promoter_genres`.

**R2DBC no tiene este mecanismo.** Es un driver reactivo puro que ejecuta SQL
exactamente como se le dice. No hay sesión, no hay dirty-checking, no hay
lazy loading. R2DBC no conoce el concepto de relaciones entre entidades.

### El patrón adoptado en GresK

La entidad `Promoter` tiene un campo `Set<MusicGenre> musicalGenres`. En base de
datos esto se almacena en una tabla de unión `promoter_genres (promoter_id, genre)`.

El adaptador `R2dbcPromoterAdapter` gestiona esta relación manualmente en dos pasos:

```java
// 1. Eliminar todos los géneros actuales del promotor
genreRepo.deleteAllByPromoterId(id)

// 2. Reinsertar todos los géneros del objeto de dominio
.thenMany(Flux.fromIterable(promoter.getMusicalGenres())
        .flatMap(genre -> genreRepo.save(
                new R2dbcPromoterGenreEntity(id, genre.name()))))
```

Este patrón **delete-all + reinsert** es intencionado:

- Es simple y predecible: no hay que calcular diffs entre el estado anterior y el nuevo.
- Es correcto para colecciones pequeñas (los géneros musicales de un promotor no
  superarán decenas de elementos).
- Para colecciones grandes se debería calcular el diff, pero ese caso no aplica aquí.

### El problema de la PK compuesta

La tabla `promoter_genres` tiene PK compuesta `(promoter_id, genre)`. Spring Data
R2DBC **no soporta PKs compuestas** mediante `@Id` sobre múltiples campos. La
solución adoptada:

```java
@Table("promoter_genres")
public class R2dbcPromoterGenreEntity implements Persistable<UUID> {

    @Id
    @Column("promoter_id")
    private UUID promoterId;   // solo este campo como @Id (no es único)

    @Column("genre")
    private String genre;

    @Override
    public boolean isNew() { return true; }  // siempre INSERT
}
```

Se declara solo `promoter_id` como `@Id` (aunque no sea único) y se implementa
`Persistable<UUID>` con `isNew() = true` siempre. Esto garantiza que Spring Data
emita siempre un INSERT, lo cual es correcto porque el adaptador **siempre borra
antes de insertar**.

### El problema del UUID asignado manualmente (entidad principal)

Spring Data R2DBC decide INSERT vs UPDATE mirando si `getId()` es null. Como el
dominio asigna el UUID antes de persistir (`PromoterId.generate()`), el ID nunca
es null → Spring Data haría siempre UPDATE, fallando en nuevos promotores.

Solución: `R2dbcPromoterEntity` también implementa `Persistable<UUID>` con un
campo `@Transient boolean isNew`. El adaptador llama a `existsById()` para
determinar si es nuevo antes de `save()`:

```java
promoterRepo.existsById(id)
    .flatMap(exists -> {
        entity.setNew(!exists);
        return promoterRepo.save(entity);
    })
```

Coste: una SELECT extra por cada save. Se acepta frente a la alternativa de
un `INSERT ... ON CONFLICT DO UPDATE` manual con `@Query` que duplicaría todos
los campos de la tabla.

### El problema de las consultas con JOIN

R2DBC no tiene join fetch. Si un promotor tiene géneros, cargarlos requiere
**dos queries independientes**:

```java
// Query 1: promotor por ID
promoterRepo.findById(uuid)
    // Query 2: géneros del promotor
    .flatMap(entity -> PromoterMapper.toDomain(entity,
            genreRepo.findGenresByPromoterId(uuid)));
```

El mapper recibe el `Flux<String> genres` como parámetro separado y lo recolecta
en el `Set<MusicGenre>` del objeto de dominio:

```java
public static Mono<Promoter> toDomain(R2dbcPromoterEntity entity, Flux<String> genres) {
    return genres
            .collect(Collectors.toCollection(LinkedHashSet::new))
            .map(genreStrings -> Promoter.reconstitute(..., musicGenres, ...));
}
```

Para `findByGenre()` se evita el problema N+1 usando un `@Query` con JOIN que
devuelve directamente `Flux<R2dbcPromoterEntity>`. Solo los géneros de cada
promotor se cargan individualmente (inevitables con R2DBC).

---

## 6. `ReactiveSecurityContextHolder` vs `SecurityContextHolder`

### Por qué el tradicional `SecurityContextHolder` está prohibido en WebFlux

`SecurityContextHolder` almacena el contexto de seguridad en un `ThreadLocal`.
En el modelo servlet, cada petición vive en un único hilo durante toda su vida,
por lo que `SecurityContextHolder.getContext()` funciona correctamente.

En WebFlux, una suscripción puede moverse entre hilos en cualquier punto del
pipeline reactivo (por ejemplo, al hacer `subscribeOn(Schedulers.boundedElastic())`).
Si el código lee `SecurityContextHolder.getContext()` en un hilo diferente al que
procesó la autenticación, el `ThreadLocal` estará vacío y la autenticación se
habrá perdido.

### `ReactiveSecurityContextHolder` y el reactor Context

Project Reactor tiene su propio mecanismo de propagación de contexto: el
**reactor Context**. Es un mapa inmutable que se propaga hacia abajo por la
cadena de operadores reactivos, independientemente del hilo de ejecución.

`ReactiveSecurityContextHolder` usa este mecanismo:

```java
// Escritura (Spring Security Filter la hace automáticamente al autenticar):
ReactiveSecurityContextHolder.withAuthentication(authentication)
// equivale a: context -> context.put(SECURITY_CONTEXT_KEY, Mono.just(new SecurityContextImpl(auth)))

// Lectura (en el controller o en el use case):
ReactiveSecurityContextHolder.getContext()
    .map(SecurityContext::getAuthentication)
    .map(auth -> (PromoterId) auth.getPrincipal())
```

El contexto viaja "hacia abajo" en la cadena de operadores. Por eso se escribe
con `contextWrite` (que opera en la suscripción, no en la emisión):

```java
chain.filter(exchange)
    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
```

### Implicación en `PromoterController`

Los endpoints `GET /me` y `PUT /me` obtienen el ID del promotor autenticado
desde el contexto reactivo, nunca desde la URL. Esto evita que el cliente
pueda falsificar su propio ID:

```java
private Mono<PromoterId> currentPromoterId() {
    return ReactiveSecurityContextHolder.getContext()
            .map(SecurityContext::getAuthentication)
            .map(auth -> (PromoterId) auth.getPrincipal());
}
```

El `Authentication.getPrincipal()` contendrá un `PromoterId` porque el JWT
filter (cuando esté implementado) extraerá el subject del token y construirá
el principal como `PromoterId`.

### Implicación en `SecurityConfig`

La configuración de seguridad usa `SecurityWebFilterChain` (WebFlux) en lugar
de `SecurityFilterChain` (servlet), con `@EnableWebFluxSecurity` en lugar de
`@EnableWebSecurity`:

```java
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/v1/promoters/register",
                                      "/api/v1/promoters/login").permitAll()
                        .pathMatchers("/api/v1/promoters/me").authenticated()
                        .anyExchange().permitAll()
                )
                .build();
    }
}
```

---

## 7. `StepVerifier` como herramienta de test reactivo y por qué `.block()` está prohibido

### El problema con `.block()` en tests

La tentación al escribir tests de use cases reactivos es llamar `.block()` para
extraer el valor del `Mono` y hacer aserciones:

```java
// MAL — NUNCA hacer esto
Promoter result = useCase.execute(query).block();
assertThat(result.getName().value()).isEqualTo("Club Test");
```

Esto es problemático por varias razones:

1. **Oculta errores.** Si el `Mono` emite un error, `.block()` lanza una excepción
   no descriptiva que no indica qué parte del pipeline falló.

2. **No testea el comportamiento reactivo.** Un `Mono` puede emitir cero, uno o
   un error. `.block()` solo verifica el caso de uno; si el `Mono` estuviese vacío,
   `.block()` devuelve `null` y el test podría pasar incorrectamente.

3. **Viola el modelo reactivo.** En producción el código nunca llama `.block()`.
   Si los tests lo hacen, prueban un comportamiento que no ocurre en producción.

4. **Puede causar deadlocks.** En Netty, llamar a `.block()` desde el event loop
   thread provoca un deadlock. Aunque en tests esto suele no ocurrir (los tests
   corren en hilos separados), es un hábito peligroso.

### `StepVerifier`: el contrato reactivo como assertion

`StepVerifier` (de `reactor-test`) permite describir la secuencia de eventos
esperada de un `Publisher` de forma declarativa:

```java
// Caso de éxito: emite exactamente un elemento y completa
StepVerifier.create(useCase.execute(command))
        .verifyComplete();

// Caso de error: emite exactamente este tipo de error
StepVerifier.create(useCase.execute(badCommand))
        .expectError(PromoterNotFoundException.class)
        .verify();

// Caso con dato: emite un elemento con esta propiedad y completa
StepVerifier.create(useCase.execute(query))
        .assertNext(promoter -> assertThat(promoter.getName().value()).isEqualTo("Club Test"))
        .verifyComplete();
```

`StepVerifier.create(publisher).verify()` bloquea el hilo del test (hilo de
JUnit) hasta que el `Publisher` completa o lanza un error, pero lo hace de forma
controlada: si el `Publisher` no emite los elementos esperados en el tiempo
definido, el test falla con un mensaje claro.

### Regla de oro

> En GresK, `.block()` en tests (y en producción) está prohibido.
> Todo test reactivo usa `StepVerifier`.
> Todo test HTTP usa `WebTestClient`.

---

## 8. Tests de la capa web en Spring Boot 4: `WebTestClient.bindToController`

### Contexto: `@WebFluxTest` fue eliminado en Spring Boot 4

Spring Boot 4.x eliminó todos los test slices de la capa web del módulo
`spring-boot-test-autoconfigure`. Solo `@JsonTest` permanece. Los slices
`@WebFluxTest`, `@WebMvcTest`, `@DataR2dbcTest` ya no existen.

### Alternativa adoptada: `WebTestClient.bindToController`

`WebTestClient` es parte de `spring-test` (Spring Framework 7) y no requiere
un contexto Spring. Con `bindToController` se levanta un dispatcher WebFlux
mínimo que incluye solo el controlador y los advisors especificados:

```java
@ExtendWith(MockitoExtension.class)
class PromoterControllerTest {

    @Mock RegisterPromoterUseCase registerUseCase;
    // ...
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        PromoterController controller = new PromoterController(
                registerUseCase, authenticateUseCase, getUseCase, updateUseCase);
        webTestClient = WebTestClient
                .bindToController(controller)
                .controllerAdvice(new GlobalExceptionHandler())
                // Registra el MutatorFilter de Spring Security Test para
                // poder usar mutateWith(mockAuthentication(...))
                .apply(SecurityMockServerConfigurers.springSecurity())
                .build();
    }
}
```

Ventajas frente a `@SpringBootTest`:
- No levanta contexto Spring → tests en ~300 ms vs ~5 s.
- No necesita base de datos ni ningún bean de infraestructura.
- Testa routing, serialización JSON, Bean Validation (`@Valid`) y el advice.

### Por qué se necesita `springSecurity()` para los tests de `/me`

`SecurityMockServerConfigurers.mockAuthentication(auth)` funciona añadiendo un
`WebFilter` (`MutatorFilter`) que escribe la autenticación en el reactor Context.
Pero ese filtro solo se activa si primero se registra la infraestructura de
Spring Security Test con `.apply(SecurityMockServerConfigurers.springSecurity())`.

Sin `springSecurity()`, el `MutatorFilter` nunca se registra → el reactor
Context no contiene el `Authentication` → `ReactiveSecurityContextHolder.getContext()`
devuelve `Mono.empty()` → `currentPromoterId()` devuelve vacío → el handler
devuelve respuesta vacía.

### El test de "sin autenticación → 401" pertenece a los tests de integración

Con `bindToController` no existe `SecurityWebFilterChain` real. Si se llama a
`/me` sin autenticación, `currentPromoterId()` devuelve `Mono.empty()`, el handler
devuelve `Mono.empty()`, y WebFlux responde 404 (no 401). El comportamiento 401
lo aplica `SecurityConfig` y se verifica en tests de integración con
`@SpringBootTest(webEnvironment = RANDOM_PORT)`.

---

## 9. Limitaciones conocidas de R2DBC vs JPA y workarounds adoptados en GresK

| Limitación R2DBC | Comportamiento JPA equivalente | Workaround en GresK |
|---|---|---|
| No hay gestión automática de relaciones | `@OneToMany` con `CascadeType.ALL` | Delete-all + reinsert manual en el adaptador |
| No hay soporte de PK compuesta con `@Id` múltiple | `@EmbeddedId` o `@IdClass` | Un solo `@Id` no único + `isNew() = true` siempre |
| No detecta si una entidad es nueva cuando el ID no es null | `entityManager.merge()` / `isNew()` automático | `Persistable<UUID>` + `existsById()` antes de cada save |
| No hay lazy loading | `FetchType.LAZY` con proxy | Dos queries explícitas (entidad + colección por separado) |
| No hay join fetch | `JOIN FETCH` en JPQL | `@Query` con JOIN para evitar N+1 en findByGenre |
| No hay transacciones con múltiples statements triviales | `@Transactional` en JPA abarca todo automáticamente | `@Transactional` en R2DBC es reactivo (`TransactionalOperator`) y se usa en el adaptador cuando la operación involucra múltiples tablas |
| No hay dirty-checking | Hibernate detecta cambios en entidades y emite UPDATE automático | El adaptador siempre llama explícitamente a `save()` con el objeto completo |
| El driver no soporta `ARRAY` de PostgreSQL de forma uniforme | JPA+Hibernate convierte arrays con `@Type` | Los géneros se almacenan en tabla de unión separada en lugar de un array de PostgreSQL |

---

## 10. Impacto en el resto del codebase

Todos los módulos futuros (artistas, venues, eventos, tickets) **deben seguir
el mismo patrón** establecido en el módulo `promoter`:

1. **Domain layer**: POJOs puros, sin dependencias reactivas.
2. **Application ports**: interfaces con `Mono<T>` / `Flux<T>`.
3. **Use cases**: `@Service` con `Mono.defer()` para envolver construcción de
   value objects, `Mono.fromCallable().subscribeOn(boundedElastic())` para
   operaciones CPU-bound (BCrypt, etc.).
4. **Infrastructure / persistence**: adaptadores `@Component` con `Persistable<UUID>`
   para entidades con UUID manual; gestión manual de relaciones N:M.
5. **Infrastructure / web**: `@RestController` con `Mono<ResponseEntity<T>>`;
   `ReactiveSecurityContextHolder` para extraer el principal autenticado.
6. **Tests de use cases**: `StepVerifier`, sin `.block()`.
7. **Tests de controllers**: `WebTestClient.bindToController` + `@ExtendWith(MockitoExtension.class)`.

---

## 11. Alternativas consideradas y descartadas

| Alternativa | Motivo del descarte |
|---|---|
| Spring MVC + JPA | Modelo bloqueante, no escala bien con picos de carga en plataformas de eventos |
| Spring MVC + JPA + async (`@Async`) | Mezcla de paradigmas bloqueante/async; las transacciones JPA no funcionan bien con `@Async` |
| WebFlux + JPA con `boundedElastic()` para todas las queries | Cada query a BD bloquea un hilo del pool; las transacciones JPA usan ThreadLocals incompatibles con reactor |
| WebFlux + R2DBC + arrays de PostgreSQL para géneros | El soporte de `text[]` en R2DBC es no uniforme entre versiones del driver; la tabla de unión es más portable |
| Exponer `CompletableFuture` en ports en lugar de `Mono` | Boilerplate extra; `CompletableFuture` no tiene operadores de composición tan ricos como Project Reactor |
