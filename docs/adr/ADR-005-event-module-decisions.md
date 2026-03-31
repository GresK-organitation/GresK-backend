# ADR-005: Event Module — Rich Domain Model, State Machine y R2DBC Dynamic Filtering

| Campo        | Valor                                        |
|--------------|----------------------------------------------|
| **Estado**   | Aceptado                                     |
| **Fecha**    | 2026-03-31                                   |
| **Autores**  | Equipo GresK Backend                         |
| **Afecta a** | Módulo `event` — todas las capas             |

---

## 1. Contexto

El módulo `event` es el núcleo de negocio de GresK: un evento musical existe en
estados bien definidos (borrador, publicado, finalizado, cancelado) y tiene reglas
de negocio que controlan cuándo puede pasar de un estado a otro y qué datos son
obligatorios para cada transición.

A diferencia del módulo `promoter`, que es principalmente CRUD, el módulo `event`
tiene **lógica de dominio no trivial**. Este ADR documenta las decisiones de diseño
tomadas para modelar esa lógica de forma que sea mantenible, testeable y resistente
a regresiones.

Las decisiones se agrupan en tres áreas:

1. **Diseño del dominio**: cómo se modela `Event` y por qué la lógica vive en el
   dominio y no en los use cases.
2. **Infraestructura R2DBC**: cómo se implementa el filtrado dinámico sin Spring
   Data Specifications (que no existen en R2DBC).
3. **Capa HTTP**: patrones reactivos en el controller y configuración de seguridad.

---

## 2. Rich domain model vs. anemic domain model

### El dilema

Cuando un modelo de dominio tiene comportamientos condicionales (publicar un evento
solo si tiene todos los campos requeridos; cancelar solo desde ciertos estados),
hay dos formas de implementarlo:

**Opción A — Anemic domain model** (modelo anémico):

```java
// Event es solo un contenedor de datos, sin comportamiento
public class Event {
    private EventStatus status;
    private Genre genre;
    // solo getters y setters
}

// La lógica vive en el use case o en un "domain service"
public class PublishEventUseCase {
    public Mono<Event> execute(String eventId, String requesterId) {
        return eventRepository.findById(id)
                .flatMap(event -> {
                    if (event.getGenre() == null) throw new IncompleteEventException(...);
                    if (event.getPrice() == null) throw new IncompleteEventException(...);
                    // ... validaciones duplicadas en cada use case que las necesite
                    event.setStatus(EventStatus.PUBLISHED);
                    return eventRepository.save(event);
                });
    }
}
```

**Opción B — Rich domain model** (modelo de dominio rico):

```java
// La lógica vive en el agregado
public final class Event {
    public void publish() {
        if (genre == null) throw new IncompleteEventException("genre is missing");
        if (price == null) throw new IncompleteEventException("price is missing");
        if (capacity == null) throw new IncompleteEventException("capacity is missing");
        if (eventDate == null) throw new IncompleteEventException("eventDate is missing");
        if (!status.canTransitionTo(EventStatus.PUBLISHED))
            throw new InvalidEventTransitionException(...);
        this.status = EventStatus.PUBLISHED;
    }
}

// El use case orquesta, no valida
public class PublishEventUseCase {
    public Mono<Event> execute(String eventId, String requesterId) {
        return eventRepository.findById(id)
                .flatMap(event -> Mono.fromCallable(() -> {
                    event.publish();   // toda la lógica aquí
                    return event;
                }))
                .flatMap(eventRepository::save);
    }
}
```

### Decisión: Rich domain model

GresK adopta el modelo de dominio rico. Las reglas de negocio que afectan a un
único agregado viven **dentro del agregado**, no en los use cases.

**Motivos:**

1. **Encapsulación real.** Las reglas de "qué se necesita para publicar" son
   invariantes del dominio, no del caso de uso concreto. Si un futuro use case
   `AutoPublishEventUseCase` necesita publicar eventos, no tiene que conocer ni
   duplicar esas reglas: llama a `event.publish()` y el dominio las aplica.

2. **Testabilidad directa.** Las reglas del dominio se pueden testear con tests
   unitarios puros sobre la clase `Event`, sin Mockito, sin Spring, sin reactivo.
   Un test que verifica `event.publish()` con `genre == null` falla en milisegundos.

3. **Prevención de regresiones.** Si la regla cambia (e.g., se añade que
   `location` es obligatoria para publicar), se cambia en un único lugar (`Event.publish()`)
   y todos los use cases heredan el cambio automáticamente.

4. **El use case es el orquestador, no el validador.** El use case se ocupa de
   buscar el agregado en el repositorio, verificar permisos de acceso (`requesterId
   == event.promoterId`) y persistir el resultado. Las reglas de negocio del
   dominio son responsabilidad del agregado.

### Regla de oro

> Un use case nunca duplica reglas de negocio que pertenecen al dominio.
> Si la lógica responde a la pregunta "¿puede este evento hacer X?",
> vive en `Event`. Si responde a "¿tiene este usuario permiso para hacer X?",
> vive en el use case.

---

## 3. Factory methods: `Event.create()` vs constructor público y `Event.reconstitute()`

### El problema con constructores públicos

Si `Event` tuviese un constructor público, cualquier parte del código podría
crear un `Event` con cualquier combinación de campos, incluyendo estados incoherentes
(p. ej., un evento con `status = PUBLISHED` pero sin `genre`).

### Decisión: factory methods con semántica explícita

`Event` tiene el constructor privado y dos factory methods públicos con semánticas
distintas:

```java
// 1. Creación de un evento nuevo — siempre empieza en DRAFT
public static Event create(String title, PromoterId promoterId) {
    return new Event(
            EventId.generate(), title, promoterId,
            null, null, null, null, null, null,
            EventStatus.DRAFT, LocalDateTime.now()
    );
}

// 2. Reconstitución desde persistencia — acepta cualquier estado guardado
public static Event reconstitute(EventId id, String title, PromoterId promoterId,
                                 Genre genre, Price price, Capacity capacity,
                                 LocalDateTime eventDate, Location location,
                                 LocalDateTime revealAt, EventStatus status,
                                 LocalDateTime createdAt) {
    return new Event(id, title, promoterId, genre, price, capacity,
            eventDate, location, revealAt, status, createdAt);
}
```

**`Event.create()`** garantiza que:
- El ID siempre se genera en el dominio (`EventId.generate()`).
- El estado inicial siempre es `DRAFT` — no es posible crear un evento publicado
  directamente.
- `createdAt` se asigna en el momento de la creación.

**`Event.reconstitute()`** es el punto de entrada **exclusivo para la capa de
infraestructura** al reconstruir un evento desde la base de datos. Acepta cualquier
estado (incluyendo `PUBLISHED` o `FINISHED`) porque ese estado ya ha sido validado
en el pasado cuando se persistió.

La declaración como `public` de `reconstitute()` es intencional: el mapper de
infraestructura (`EventMapper`) vive en un paquete diferente y necesita llamarlo.
No se puede usar `package-private` porque la arquitectura hexagonal separa dominio
e infraestructura en paquetes distintos.

### Analogía con el módulo `promoter`

Este patrón es idéntico al usado en `Promoter.reconstitute()` del módulo promoter.
Es el patrón estándar de reconstrucción de agregados desde persistencia en GresK.

---

## 4. `EventStatus` como máquina de estados autónoma

### El problema

Las transiciones de estado válidas de un evento son:

```
DRAFT → PUBLISHED, CANCELLED
PUBLISHED → FINISHED, CANCELLED
FINISHED → (terminal)
CANCELLED → (terminal)
```

Hay dos formas de implementar esta lógica:

**Opción A — Switch en el servicio o use case:**

```java
// En PublishEventUseCase o en Event.publish()
switch (event.getStatus()) {
    case DRAFT -> event.setStatus(PUBLISHED);
    case PUBLISHED, FINISHED, CANCELLED ->
        throw new InvalidEventTransitionException(...);
}
```

**Opción B — El enum conoce sus propias transiciones:**

```java
public enum EventStatus {
    DRAFT, PUBLISHED, FINISHED, CANCELLED;

    private static final Map<EventStatus, Set<EventStatus>> ALLOWED = new HashMap<>();

    static {
        ALLOWED.put(DRAFT,      EnumSet.of(PUBLISHED, CANCELLED));
        ALLOWED.put(PUBLISHED,  EnumSet.of(FINISHED, CANCELLED));
        ALLOWED.put(FINISHED,   EnumSet.of());
        ALLOWED.put(CANCELLED,  EnumSet.of());
    }

    public boolean canTransitionTo(EventStatus target) {
        return ALLOWED.getOrDefault(this, EnumSet.noneOf(EventStatus.class)).contains(target);
    }
}
```

### Decisión: la tabla de transiciones vive en `EventStatus`

**Motivos:**

1. **Single source of truth.** La tabla de transiciones es una regla del dominio,
   no del código de negocio de `publish()`. Si mañana se añade el estado `SUSPENDED`
   (publicado pero temporalmente oculto), se modifica únicamente `EventStatus` y
   todos los métodos de transición (`publish()`, `finish()`, `cancel()`) lo heredan
   automáticamente a través de `canTransitionTo()`.

2. **Evita el switch duplicado.** Si la lógica fuese un switch en `Event.publish()`,
   habría que replicarla en `Event.finish()` y `Event.cancel()`. Con `canTransitionTo()`
   los tres métodos comparten la misma fuente.

3. **Testabilidad aislada.** La tabla de transiciones se puede testear directamente
   sobre `EventStatus` sin necesitar un `Event` completo.

---

## 5. Por qué `revealAt` está en el esquema del Sprint 3 sin usarse hasta el Sprint 4

### El problema

El campo `revealAt` (fecha a partir de la cual el evento es visible públicamente)
es una funcionalidad del Sprint 4, no del Sprint 3. Sin embargo, la columna
`reveal_at TIMESTAMPTZ` ya existe en la migración `V5__create_events.sql`.

### Decisión: incluir `revealAt` en la migración desde el Sprint 3

**Motivo: el coste de las migraciones de esquema.**

Las migraciones de Flyway son irreversibles en producción. Añadir una columna a
una tabla existente en producción con datos requiere:

1. Una migración `ALTER TABLE events ADD COLUMN reveal_at TIMESTAMPTZ;`.
2. Verificar que la columna se añade sin bloquear la tabla (en PostgreSQL, `ADD COLUMN`
   con valor por defecto `NULL` es instantáneo, pero no siempre es así en todas
   las versiones).
3. Coordinar el despliegue con los cambios de código del Sprint 4.

Cuando la tabla se crea en el Sprint 3 y todavía no tiene datos de producción,
incluir la columna desde el principio es de coste cero y elimina la migración
adicional en el Sprint 4.

**El campo `revealAt` en el dominio y en la persistencia está preparado**, pero
la lógica de filtrado (`WHERE reveal_at IS NULL OR reveal_at <= NOW()`) se
implementará en el Sprint 4 cuando la feature se active.

---

## 6. Filtrado dinámico con `DatabaseClient` y `StringBuilder`

### El problema: R2DBC no tiene Spring Data Specifications

En Spring Data JPA, los filtros opcionales se implementan con `JpaSpecificationExecutor`
y `Specification<T>`: objetos tipados que se componen con `and()` / `or()` y que
el framework traduce a SQL automáticamente.

**Spring Data R2DBC no tiene este mecanismo.** No existe `R2dbcSpecificationExecutor`
ni ningún equivalente. El mecanismo de consulta de Spring Data R2DBC (`@Query`,
`findBy...`) no soporta cláusulas WHERE dinámicas sin código Java.

### Decisión: `DatabaseClient` + `StringBuilder` + parámetros nombrados

```java
private Map<String, Object> buildFilterParams(EventFilter filter, StringBuilder sql) {
    Map<String, Object> params = new LinkedHashMap<>();

    filter.status().ifPresent(s -> {
        sql.append(" AND status = :status");
        params.put("status", s.name());
    });
    filter.genre().ifPresent(g -> {
        sql.append(" AND genre = :genre");
        params.put("genre", g.name());
    });
    filter.city().ifPresent(c -> {
        sql.append(" AND city = :city");
        params.put("city", c);
    });
    // ...
    return params;
}
```

El SQL se construye como:

```
SELECT * FROM events WHERE 1=1 [AND status = :status] [AND genre = :genre] ...
ORDER BY event_date ASC NULLS LAST LIMIT :limit OFFSET :offset
```

**Por qué `WHERE 1=1`:** permite añadir cláusulas `AND` sin condicionar la primera
con `WHERE` y las siguientes con `AND`. Simplifica el código de construcción.

**Por qué parámetros nombrados (`:status`) y no interpolación de strings:**
La interpolación directa (`"AND status = '" + s.name() + "'"`) introduce
vulnerabilidades de SQL Injection. Los parámetros nombrados delegan el escape al
driver R2DBC, que los trata como literales seguros independientemente de su contenido.

**Por qué `LinkedHashMap`:** preserva el orden de inserción de los parámetros,
lo cual facilita la depuración y hace que el SQL generado sea determinista y
predecible en los logs.

### Alternativas descartadas

| Alternativa | Motivo del descarte |
|---|---|
| `@Query` con múltiples métodos (`findByGenre`, `findByCity`, `findByGenreAndCity`...) | Explosión combinatoria: N filtros → 2^N métodos |
| `QuerydslPredicateExecutor` | No está disponible para R2DBC |
| `jOOQ` | Dependencia externa; añade complejidad al build. Justificado para sistemas con queries muy complejas; excesivo para este caso |
| `r2dbc-spi` directo | `DatabaseClient` ya abstrae `r2dbc-spi`; bajar a ese nivel añade boilerplate sin beneficio |

---

## 7. `Mono.zip()` para consultas paralelas en `GET /events`

### El problema

El endpoint `GET /events` devuelve una página de resultados con metadatos de
paginación:

```json
{
  "content": [...],
  "totalElements": 142,
  "page": 0,
  "size": 20,
  "totalPages": 8
}
```

Esto requiere dos queries a la base de datos:
1. `SELECT * FROM events WHERE ... LIMIT 20 OFFSET 0` (los datos de la página)
2. `SELECT COUNT(*) FROM events WHERE ...` (el total)

### Opción A — Secuencial (incorrecta)

```java
return listUseCase.execute(filter, pageRequest)
        .map(mapper::toResponse)
        .collectList()
        .flatMap(events ->
            listUseCase.count(filter)
                .map(total -> PageResponse.of(events, total, pageRequest))
        );
```

El `flatMap` espera a que `collectList()` complete (primera query) antes de
lanzar `count()` (segunda query). Las dos queries se ejecutan en serie.

### Decisión: `Mono.zip()` para ejecución paralela

```java
return Mono.zip(
        listUseCase.execute(filter, pageRequest).map(mapper::toResponse).collectList(),
        listUseCase.count(filter)
).map(tuple -> ResponseEntity.ok(
        PageResponse.of(tuple.getT1(), tuple.getT2(), pageRequest)));
```

`Mono.zip()` suscribe a ambos publishers **simultáneamente** y emite cuando
ambos han completado. Las dos queries van a la base de datos en paralelo a través
del pool de conexiones R2DBC.

**Beneficio:** La latencia total es `max(T_list, T_count)` en lugar de
`T_list + T_count`. En una tabla con muchos registros, `COUNT(*)` puede ser
más lento que la query paginada; con `zip()` el usuario no paga ese coste adicional.

---

## 8. `Mono.fromCallable()` como wrapper seguro para métodos de dominio que lanzan excepciones

### El problema

Los métodos de dominio que validan invariantes (`event.publish()`, `Capacity.of()`,
`PromoterId.of()`) lanzan excepciones síncronas cuando las invariantes se violan.

En una cadena reactiva, una excepción lanzada dentro de una lambda de `map()` o
`flatMap()` **no siempre se captura correctamente**:

```java
// INCORRECTO — la excepción lanzada en fromCallable puede salir del contexto reactivo
.flatMap(event -> {
    event.publish();   // lanza IncompleteEventException si faltan campos
    return Mono.just(event);
})
```

Si `event.publish()` lanza dentro de un `flatMap`, Project Reactor sí la captura
y la convierte en un error del `Mono` — en este caso concreto funciona. Pero
hay contextos donde una excepción lanzada síncronamente fuera de un operador
reactivo no se captura y termina propagándose fuera de la cadena reactiva.

La práctica recomendada en Project Reactor es envolver **cualquier código que
puede lanzar** en `Mono.fromCallable()`:

```java
// CORRECTO — la excepción queda encapsulada en el Mono como señal de error
.flatMap(event -> Mono.fromCallable(() -> {
    event.publish();   // si lanza, el Mono emite onError
    return event;
}))
```

### Decisión: `Mono.fromCallable()` para toda lógica de dominio que puede lanzar

`Mono.fromCallable(Callable<T>)` ejecuta el `Callable` en el hilo del suscriptor
y, si lanza una excepción, la convierte en una señal `onError` del `Mono`.
Esto garantiza que **todas las excepciones del dominio viajan por el canal reactivo**
y pueden ser interceptadas por operadores como `onErrorResume()`, o en última
instancia por el `@RestControllerAdvice`.

El patrón en GresK es:

```java
// En los use cases — para lógica de dominio que puede lanzar
return Mono.fromCallable(() -> {
    Event event = Event.create(command.title(), promoterId);
    // fluent setters también pueden lanzar (e.g., Price con amount <= 0)
    return event;
}).flatMap(eventRepository::save);
```

### `Mono.defer()` como envoltorio exterior

Los use cases usan `Mono.defer()` como envoltorio exterior para que la construcción
de value objects (que ocurre antes del primer operador reactivo) también quede
dentro de la cadena reactiva:

```java
public Mono<Event> execute(CreateEventCommand command) {
    return Mono.defer(() -> {
        // Si PromoterId.of(command.promoterId()) lanza IllegalArgumentException,
        // el error queda capturado en el Mono, no se propaga síncronamente.
        PromoterId promoterId = PromoterId.of(command.promoterId());
        return Mono.fromCallable(() -> Event.create(...))
                .flatMap(eventRepository::save);
    });
}
```

Sin `Mono.defer()`, la excepción en `PromoterId.of()` se propagaría síncronamente
al caller antes de que se cree ningún `Mono`, rompiendo el contrato reactivo del
método.

---

## 9. `@EnableReactiveMethodSecurity` es obligatorio en WebFlux para que `@PreAuthorize` funcione

### El síntoma del fallo silencioso

En Spring MVC, `@EnableMethodSecurity` (antes `@EnableGlobalMethodSecurity`) se
activa automáticamente con `spring-boot-starter-security` en muchas configuraciones.

En Spring WebFlux, **`@EnableReactiveMethodSecurity` NO se activa por defecto**.
Si se añaden anotaciones `@PreAuthorize` a los controllers sin esta anotación en
la configuración de seguridad, el comportamiento es:

- Las anotaciones `@PreAuthorize` se ignoran silenciosamente.
- Todas las peticiones pasan, independientemente del rol del usuario autenticado.
- No hay ningún error de compilación ni de arranque que alerte del problema.

Este es uno de los fallos de configuración más difíciles de detectar en WebFlux
porque todo compila y arranca correctamente.

### Decisión: añadir `@EnableReactiveMethodSecurity` explícitamente

```java
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity   // ← activa @PreAuthorize en WebFlux
public class SecurityConfig {
    // ...
}
```

Con esta anotación, Spring Security registra un `MethodSecurityInterceptor`
reactivo que evalúa las expresiones de `@PreAuthorize` **dentro del contexto
reactivo**, usando `ReactiveSecurityContextHolder` para obtener la autenticación
actual.

### Uso en `EventController`

```java
@PostMapping
@PreAuthorize("hasRole('PROMOTER')")
public Mono<ResponseEntity<EventResponse>> create(...) { ... }

@GetMapping("/{id}")
@PreAuthorize("isAuthenticated()")
public Mono<ResponseEntity<EventResponse>> getById(...) { ... }
```

`hasRole('PROMOTER')` verifica que el `Authentication` actual tenga la autoridad
`ROLE_PROMOTER`. `isAuthenticated()` verifica que exista un `Authentication`
válido (no anónimo). Ambas expresiones son evaluadas de forma reactiva sin
bloquear.

### Por qué `anyExchange().permitAll()` en `SecurityConfig` es temporal

La configuración actual permite todas las rutas no declaradas explícitamente
(`anyExchange().permitAll()`). La protección granular de los endpoints de eventos
se delega a `@PreAuthorize` a nivel de método. Cuando el JWT filter esté
completamente implementado y validado, el `anyExchange().permitAll()` se cambiará
a `anyExchange().authenticated()` para añadir una segunda capa de protección.

---

## 10. `WebExchangeBindException` como equivalente de `MethodArgumentNotValidException` en WebFlux

### El contexto

En Spring MVC, cuando un `@RequestBody` anotado con `@Valid` falla la validación
de Bean Validation, el framework lanza `MethodArgumentNotValidException`. Esta
excepción es capturada por `@ExceptionHandler` o `@ControllerAdvice` para devolver
un 400 con los detalles del error.

En Spring WebFlux, **la excepción equivalente es `WebExchangeBindException`**. Es
la que Spring WebFlux lanza cuando `@Valid` detecta violaciones de constrainst
(`@NotBlank`, `@NotNull`, `@DecimalMin`, `@Min`, `@Future`, etc.) en el body
de la petición.

### Por qué no se maneja en `EventExceptionHandler`

`WebExchangeBindException` ya es manejada por el `GlobalExceptionHandler` del
módulo `promoter`:

```java
// En GlobalExceptionHandler (módulo promoter, reutilizado globalmente)
@ExceptionHandler(WebExchangeBindException.class)
public Mono<ResponseEntity<Map<String, Object>>> handleValidation(
        WebExchangeBindException ex) { ... }
```

Al ser un `@RestControllerAdvice` sin `basePackages` restringido, aplica a todos
los controllers de la aplicación, incluido `EventController`. No es necesario
duplicarlo en `EventExceptionHandler`.

`EventExceptionHandler` solo captura las excepciones de dominio específicas del
módulo `event`:

| Excepción | HTTP Status | Causa |
|---|---|---|
| `EventNotFoundException` | 404 | El evento con el ID solicitado no existe |
| `IncompleteEventException` | 422 | Se intenta publicar un evento sin todos los campos requeridos |
| `ForbiddenOperationException` | 403 | El promotor que hace la petición no es el dueño del evento |

Esta separación mantiene el módulo `event` desacoplado del módulo `promoter`:
el handler global gestiona errores transversales (validación, auth) y el handler
del módulo gestiona excepciones del dominio propio.

---

## 11. Consecuencias

### Lo que este diseño habilita

- **Testabilidad total de la lógica de dominio**: `Event.publish()`, `Capacity.of()`,
  `EventStatus.canTransitionTo()` se pueden testear con tests unitarios simples en
  milisegundos, sin Spring, sin base de datos, sin Mockito.

- **Extensibilidad de la máquina de estados**: añadir un nuevo estado (e.g., `DRAFT_REVIEW`)
  requiere modificar solo `EventStatus` (nueva entrada en la tabla de transiciones)
  y `Event` (nuevo método de transición). Los use cases y el controller no cambian.

- **Queries de listado eficientes**: el patrón `DatabaseClient` + `StringBuilder`
  soporta cualquier combinación de filtros sin explosión de métodos en el repositorio.
  El `Mono.zip()` garantiza que la paginación no introduce latencia adicional.

- **Seguridad verificable**: con `@EnableReactiveMethodSecurity`, las reglas de
  acceso de `@PreAuthorize` se pueden testear con `WebTestClient` +
  `mockAuthentication()` de forma aislada.

### Trade-offs y limitaciones

- **`existsById()` antes de cada `save()`**: el patrón `Persistable<UUID>` requiere
  una SELECT extra por cada operación de escritura. En el módulo `event` esto
  aplica a `EventRepository.save()`. Es el mismo trade-off documentado en ADR-004
  para el módulo `promoter`.

- **`Mono.fromCallable()` ejecuta síncronamente**: `fromCallable` no cambia de
  scheduler. Si `event.publish()` fuese una operación CPU-intensiva (no es el caso),
  habría que añadir `.subscribeOn(Schedulers.boundedElastic())`. Para la lógica
  de dominio actual (validaciones de nulos y comparaciones de enum), el overhead
  es despreciable.

- **`revealAt` sin lógica activa**: el campo existe en el esquema y en el modelo
  de dominio, pero `R2dbcEventAdapter.buildFilterParams()` no lo filtra todavía.
  Un desarrollador que lea el código sin este ADR podría preguntarse por qué
  existe el campo sin usarse. La respuesta es el argumento de coste de migración
  del Sprint 3 documentado en la sección 5.

- **`anyExchange().permitAll()` es temporal**: la protección a nivel de ruta es
  incompleta hasta que el JWT filter esté implementado y se cambie a
  `anyExchange().authenticated()`. Durante el desarrollo, `@PreAuthorize` a nivel
  de método es la única barrera de autorización activa.
