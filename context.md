# GresK Backend — Context

## Stack
- **Java 21** + **Spring Boot 3.4.3**
- **Spring Security** + **JWT** (jjwt 0.12.6)
- **Spring Data JPA** + **PostgreSQL** + **Flyway**
- **Cloudinary** (cloudinary-http5 2.0.0) — gestión de imágenes
- **Lombok**, **SpringDoc/OpenAPI 2.8.5**
- **Maven**

---

## Arquitectura: Hexagonal estricta

```
Infrastructure Input (Controllers, Listeners)
        ↓
Application (UseCases, Commands, DTOs, Ports In)
        ↓
Domain (Model, VOs, Ports Out, Exceptions)
        ↓
Infrastructure Output (JPA Adapters, Cloudinary, etc.)
```

### Reglas consolidadas
- Los **controllers** solo manejan UUIDs y DTOs primitivos. Sin lógica de orquestación.
- Los **use cases** crean los VOs internamente a partir del command.
- Los **commands** son primitivos (String, UUID, Set<String>). `MultipartFile` permitido por pragmatismo.
- Los **mappers de persistencia** usan métodos de mutación explícitos en las entities (`updateProfile()`, `updateLogo()`, etc.), nunca `@Setter` global.
- **Dirty checking** en todos los adapters: buscar entity existente → mutar → save. No crear entity nueva en cada save.
- `Instant` en lugar de `LocalDateTime` en todas las entities.
- `@Version` para optimistic locking en todas las entities mutables.
- `FetchType.LAZY` en todos los `@ElementCollection`.
- `Set` en lugar de `List` para colecciones sin duplicados.
- Excepciones de dominio propias por módulo.
- **Flyway** para migraciones.

---

## Estructura de packages

```
com.gresk
├── infrastructure/                  ← técnica transversal (Spring Security)
│   └── security/
│       ├── SecurityConfig.java
│       ├── JwtAuthenticationFilter.java
│       └── SecurityContextService.java
├── shared/                          ← shared kernel de dominio
│   └── domain/
│       ├── AccountStatus.java       (PENDING, ACTIVE, SUSPENDED, DELETED)
│       ├── MusicGenre.java          (enum con .key())
│       ├── Role.java                (USER, ARTIST, PROMOTER, PROMOTER_PENDING, ADMIN)
│       ├── event/
│       │   ├── UserRegisteredEvent.java
│       │   └── PromoterRegisteredEvent.java
│       ├── valueobject/
│       │   ├── Email.java
│       │   ├── Name.java
│       │   ├── Description.java
│       │   ├── Address.java         (street, City, country)
│       │   ├── City.java
│       │   ├── AssetId.java         (en fichero ImageId.java — naming mismatch)
│       │   ├── ImageUrl.java
│       │   ├── Password.java
│       │   └── Coordinates.java
│       └── port/out/
│           ├── ImageUrlResolverPort.java
│           └── ImageStoragePort.java
│   └── infrastructure/
│       ├── config/
│       │   └── CloudinaryConfig.java
│       ├── out/
│       │   ├── ImageUrlResolverAdapter.java
│       │   └── CloudinaryImageStorageAdapter.java
│       └── web/
│           └── GlobalExceptionHandler.java
└── modules/
    ├── identity/
    ├── user/
    ├── promoter/
    └── event/                       ← pendiente de implementar
```

---

## Módulo: Identity (account)

**Responsabilidad:** Seguridad. Login, registro, JWT, roles, password encriptada.

### Eventos que publica
- `UserRegisteredEvent` → escuchado por User module
- `PromoterRegisteredEvent` → escuchado por Promoter module

### Estructura interna
```
identity/
├── application/
│   ├── command/
│   │   ├── RegisterUserAccountCommand     (email, rawPassword, name, description, city, musicGenres, MultipartFile avatar)
│   │   ├── RegisterPromoterAccountCommand (email, rawPassword, companyName, country, address, city, description, musicalGenres, phone, website, MultipartFile logo)
│   │   └── LoginCommand
│   ├── port/in/
│   │   ├── RegisterUserAccountUseCase
│   │   └── LoginUseCase
│   ├── port/out/
│   │   ├── PasswordHasherPort
│   │   └── JwtTokenGeneratorPort
│   └── usecase/
│       ├── RegisterUserAccountUseCaseImpl   (publica UserRegisteredEvent, sube avatar a Cloudinary si present)
│       ├── RegisterPromoterAccountUseCase   (publica PromoterRegisteredEvent, sube logo a Cloudinary si present)
│       ├── LoginUseCaseImpl
│       └── GetEmailUseCase
├── domain/
│   ├── model/
│   │   ├── Account.java     (id, email, passwordHash, roles, status, createdAt)
│   │   └── AccountId.java
│   ├── exception/
│   │   ├── AccountAlreadyExistsException
│   │   └── InvalidAccountCredentialsException
│   └── port/out/
│       └── AccountRepositoryPort
└── infrastructure/
    ├── persistence/
    │   ├── AccountEntity.java
    │   ├── AccountJpaRepository.java
    │   ├── AccountMapper.java
    │   └── JpaAccountRepositoryAdapter.java
    ├── security/
    │   └── BcryptPasswordHasher.java
    └── web/
        ├── AuthController.java
        └── dto/
            ├── RegisterUserAuthRequest.java    (multipart/form-data: @RequestPart("data"))
            ├── RegisterPromoterAuthRequest.java (multipart/form-data: @RequestPart("data"))
            ├── LoginRequest.java
            └── AuthResponse.java
```

### Endpoints
```
POST /api/v1/auth/register/user      multipart/form-data  @RequestPart("data") + @RequestPart("avatar", required=false)
POST /api/v1/auth/register/promoter  multipart/form-data  @RequestPart("data") + @RequestPart("logo", required=false)
POST /api/v1/auth/login              application/json
GET  /api/v1/auth/check-email?email=
```

---

## Módulo: User (Fan)

**Responsabilidad:** Perfil del asistente, gamificación, recomendaciones.

### Dominio
```java
User {
    UserId id                    // == accountId (identidad federada)
    Email email
    Name name
    Description description
    City city
    AssetId avatarAssetId        // nullable, resuelto vía ImageUrlResolverPort
    Set<MusicGenre> musicGenres
    AccountStatus status
    UserTier tier                // FREE | PREMIUM
    int loyaltyPoints
    Set<Role> roles
    Instant createdAt
}
```

Métodos de dominio: `create()`, `create(+AssetId)`, `reconstitute()`, `updateProfile()`, `updateAvatar(AssetId)`, `addPoints()`, `suspendAccount()`, `reactivateAccount()`, `deleteAccount()`.

### Listener
`UserModuleEventListener` escucha `UserRegisteredEvent` → llama `RegisterUserUseCase`.

### Endpoints
```
GET   /api/v1/users/me/dashboard
PUT   /api/v1/users/me
PATCH /api/v1/users/me/avatar   multipart/form-data  @RequestPart("file")
```

### Estructura interna
```
user/
├── application/
│   ├── command/
│   │   ├── RegisterUserCommand   (userId, email, name, description, city, musicGenres, avatarAssetId)
│   │   ├── UpdateUserCommand
│   │   └── AddPointsCommand
│   ├── dto/
│   │   ├── UserDashboardDTO
│   │   ├── EventRecommendedDTO
│   │   └── MusicRecommendedDTO
│   └── usecase/
│       ├── RegisterUserUseCaseImpl
│       ├── UpdateUserUseCaseImpl
│       ├── GetUserDashboardUseCaseImpl
│       ├── AddPointsUseCaseImpl
│       └── UpdateUserAvatarUseCaseImpl
├── domain/
│   ├── model/
│   │   ├── User.java
│   │   ├── UserId.java
│   │   └── UserTier.java
│   ├── exception/ ...
│   └── port/
│       ├── in/  RegisterUserUseCase, UpdateUserUseCase, GetUserDashboardUseCase,
│       │        UpdateUserAvatarUseCase
│       └── out/ UserRepositoryPort, EventRecommendationProvider, MusicRecommendationProvider
└── infrastructure/
    ├── event/UserModuleEventListener.java
    ├── persistence/ JpaUserRepositoryAdapter, UserEntity, UserPersistenceMapper, JpaUserRepository
    └── in/rest/ UserController, UpdateUserProfileRequest, UserDashboardResponseDTO, UserRestMapper
```

---

## Módulo: Promoter

**Responsabilidad:** Perfil profesional del organizador, reputación, dashboard.

### Dominio
```java
Promoter {
    PromoterId id                // == accountId (identidad federada)
    Email email
    Name name
    Description description
    Address address              // street + City + country
    AssetId logoAssetId          // nullable, resuelto vía ImageUrlResolverPort
    Set<MusicGenre> musicalGenres
    AccountStatus status         // empieza en PENDING
    Instant createdAt
}
```

Métodos de dominio: `create()`, `create(+AssetId+genres)`, `reconstitute()`, `activate()`, `suspend()`, `updateBasicInfo()`, `updateLogo(AssetId)`, `replaceGenres(Set<MusicGenre>)`, `addGenre()`, `deleteGenre()`.

### Listener
`PromoterModuleEventListener` escucha `PromoterRegisteredEvent` → llama `RegisterPromoterPort`.

### Ports out (dominio)
- `PromoterRepositoryPort`
- `PromoterStatsProviderPort` → `getStatsByPromoterId(PromoterId)` → devuelve `PromoterStats(totalRevenue, totalEvents, averageRating)`. Implementado por el módulo Ticket/Event.

### Endpoints
```
GET   /api/v1/promoters/me
GET   /api/v1/promoters/me/dashboard
PUT   /api/v1/promoters/me
PATCH /api/v1/promoters/me/logo   multipart/form-data  @RequestPart("file")
```

### DTOs de aplicación
- `PromoterProfileDTO` — devuelto por `GetPromoterByAccountIdPort` (con logoUrl ya resuelto)
- `PromoterDashboardDTO` — name, logoUrl, description, street, city, country, musicalGenres, averageRating

### Estructura interna
```
promoter/
├── application/
│   ├── command/
│   │   ├── RegisterPromoterCommand  (promoterId, email, name, street, city, country, description, musicalGenres, logoAssetId)
│   │   ├── UpdatePromoterProfileCommand (promoterId, name, street, city, country, description, Set<String> musicalGenres)
│   │   └── (no UpdatePromoterLogoCommand — la lógica va en el use case directamente)
│   ├── dto/
│   │   ├── PromoterProfileDTO
│   │   └── PromoterDashboardDTO
│   ├── port/in/
│   │   ├── RegisterPromoterPort
│   │   ├── GetPromoterByAccountIdPort  → PromoterProfileDTO (logoUrl ya resuelto internamente)
│   │   ├── GetPromoterDashboardPort    → PromoterDashboardDTO
│   │   ├── UpdatePromoterProfilePort
│   │   ├── UpdatePromoterLogoPort
│   │   └── VerifyPromoterPort
│   └── usecase/
│       ├── RegisterPromoterUseCase
│       ├── GetPromoterByAccountIdUseCase   (inyecta ImageUrlResolverPort)
│       ├── GetPromoterDashboardUseCase     (inyecta ImageUrlResolverPort + PromoterStatsProviderPort)
│       ├── UpdatePromoterProfileUseCase
│       ├── UpdatePromoterLogoUseCase       (inyecta ImageStoragePort)
│       └── VerifyPromoterUseCase
├── domain/
│   ├── model/
│   │   ├── Promoter.java
│   │   └── valueobject/
│   │       ├── PromoterId.java
│   │       └── PromoterStats.java  (totalRevenue, totalEvents, averageRating)
│   ├── exception/ ...
│   └── port/out/
│       ├── PromoterRepositoryPort
│       └── PromoterStatsProviderPort
└── infrastructure/
    ├── event/PromoterModuleEventListener.java
    ├── persitence/   [typo en el package name — existe así]
    │   ├── PromoterEntity.java
    │   ├── PromoterJpaRepository.java
    │   ├── PromoterMapper.java
    │   └── JpaPromoterRepositoryAdapter.java
    └── web/
        ├── PromoterController.java
        ├── PromoterResponse.java        (from(PromoterProfileDTO))
        └── UpdatePromoterProfileRequest.java
```

---

## Módulo: Event — Pendiente

**Responsabilidad:** Inventario de conciertos. Venue con coordenadas, stock de entradas, categorías.

### Pendiente de definir
- Agregado `Event` (coordenadas para mapa, stock, categorías)
- `EventRecommendationProvider` (port/out) → usado por User para filtrar por ciudad/géneros
- `PromoterStatsProviderPort` implementado aquí → calcula `averageRating` y `totalRevenue`
- Comunicación con Ticket para conteo de ventas

---

## Módulo: Ticket — Pendiente

**Responsabilidad:** Compra de entradas, recaudación.

### Diseño decidido
- Opción A (actual): Query directa a Ticket vía `TicketStatsProviderPort` (port/out en Event, implementado por Ticket). Devuelve `ticketsSold` y `totalRevenue` por evento.
- Cuando un ticket se compra → `TicketPurchasedEvent(eventId, ticketId, price, buyerId, purchasedAt)`
- NO acumular `ticketsSold` en el agregado `Event` (contención bajo carga)

---

## Flujos clave

### Registro de User
```
POST /auth/register/user (multipart)
  → AuthController
  → RegisterUserAccountUseCaseImpl
      → sube avatar a Cloudinary (si present) → assetId
      → crea Account
      → publica UserRegisteredEvent(userId, email, name, description, city, genres, avatarAssetId)
      → guarda Account
  → UserModuleEventListener.on(UserRegisteredEvent)
  → RegisterUserUseCaseImpl
      → crea User con AssetId
      → guarda User
```

### Registro de Promoter
```
POST /auth/register/promoter (multipart)
  → AuthController
  → RegisterPromoterAccountUseCase
      → sube logo a Cloudinary (si present) → assetId
      → crea Account (status=PENDING, role=PROMOTER_PENDING)
      → publica PromoterRegisteredEvent(promoterId, email, companyName, ..., phone, website, logoAssetId)
      → guarda Account
  → PromoterModuleEventListener.on(PromoterRegisteredEvent)
  → RegisterPromoterUseCase
      → crea Promoter con AssetId y genres
      → guarda Promoter
```

### Resolución de imagen
```
DB: logoAssetId = "promoters/logos/abc123"
  → ImageUrlResolverPort.resolveOrDefault(assetId)
  → template: "https://res.cloudinary.com/dzfgoh6hu/image/upload/.../promoters/logos/abc123"
  → frontend recibe URL lista
```

### Dashboard de Promoter (GET /me/dashboard)
```
GetPromoterDashboardUseCase
  → PromoterRepositoryPort.findById(id)
  → PromoterStatsProviderPort.getStatsByPromoterId(id) → averageRating
  → ImageUrlResolverPort.resolveOrDefault(logoAssetId)
  → PromoterDashboardDTO(name, logoUrl, description, street, city, country, genres, averageRating)
```

---

## Identidad Federada

`PromoterId == UserId == AccountId` — mismo UUID compartido entre módulos.
Al crear en Identity → el id del Account se propaga vía evento a User/Promoter.
**Nunca** usar `PromoterId.generate()` en los use cases de registro — siempre usar el id del evento.

---

## Imágenes (Cloudinary)

| Puerto | Implementación | Usado por |
|--------|---------------|-----------|
| `ImageStoragePort` | `CloudinaryImageStorageAdapter` | Identity use cases (registro), `UpdatePromoterLogoUseCase`, `UpdateUserAvatarUseCaseImpl` |
| `ImageUrlResolverPort` | `ImageUrlResolverAdapter` | `GetPromoterByAccountIdUseCase`, `GetPromoterDashboardUseCase` |

Configuración en `application.yml`:
```yaml
cloudinary:
  cloud-name: ${CLOUDINARY_CLOUD_NAME}
  api-key: ${CLOUDINARY_API_KEY}
  api-secret: ${CLOUDINARY_API_SECRET}

gresk:
  images:
    default-url: "https://res.cloudinary.com/..."
    asset-url-template: "https://res.cloudinary.com/.../{assetId}"
```

---

## Migraciones Flyway

| Versión | Contenido |
|---------|-----------|
| V1 | init |
| V2 | create user tables |
| V4 | create promoters table |
| V5 | create events |
| V6 | create tickets table |
| V7 | create accounts table |
| V8 | migrate promoter credentials to accounts |
| V9 | migrate user credentials to accounts |
| V10 | add promoter logo url |
| V11 | migrate promoter logo to asset_id |
| V12 | add user avatar_asset_id |

---

## Convenciones de código

```java
// Ports In → en application/port/in/
// Ports Out (dominio) → en domain/port/out/

// Use cases implementan el port in
@Service
@RequiredArgsConstructor
public class XxxUseCase implements XxxPort {

// Adapters de persistencia → dirty checking obligatorio
public X save(X domain) {
    Entity entity = repository.findById(domain.getId().value())
        .map(existing -> { existing.updateXxx(...); return existing; })
        .orElseGet(() -> mapper.toEntity(domain));
    return mapper.toDomain(repository.save(entity));
}

// Response DTOs → factory method estático from()
public static XxxResponse from(XxxDTO dto) { ... }

// Nunca ImageUrlResolverPort en controllers
// La URL se resuelve en el use case, el controller recibe DTO con URL ya lista
```

---

## Issues conocidos / Technical debt

- Package `persitence` (typo) en promoter — existe así, no renombrar sin migrar imports
- `ImageId.java` contiene la clase `AssetId` — naming mismatch en el fichero
- `RegisterPromoterAuthRequest` tiene campos `phone` y `website` añadidos manualmente por el dev (reflejados en `PromoterRegisteredEvent`)
- `PromoterStats.totalRevenue` y `totalEvents` retornan 0 hasta que Ticket esté implementado
