# Gresk Backend

API REST del proyecto Gresk, construida con Spring Boot 3, Java 21 y PostgreSQL 15.

---

## Requisitos

- [Docker](https://www.docker.com/products/docker-desktop) (incluye Docker Compose)
- Java 21
- Maven 3.9+

---

## Levantar el entorno local

### 1. Configurar variables de entorno

```bash
cp .env.example .env
```

Edita `.env` con tus valores locales. Este archivo **nunca** se sube al repositorio.

### 2. Levantar la base de datos

```bash
docker compose up -d
```

Esto levanta:
| Servicio   | URL                        | Descripción             |
|------------|----------------------------|-------------------------|
| PostgreSQL | `localhost:5432`           | Base de datos principal |
| pgAdmin    | http://localhost:5050      | Interfaz web de la BD   |

> pgAdmin solo arranca cuando PostgreSQL esté healthy (healthcheck configurado).

### 3. Arrancar la aplicación Spring Boot

```bash
./mvnw spring-boot:run
```

> Asegúrate de tener las variables de `.env` exportadas en tu terminal, o usa el plugin **EnvFile** de IntelliJ para cargarlas automáticamente.

---

## Parar el entorno

```bash
docker compose down        # Para los contenedores (los datos persisten)
docker compose down -v     # Para los contenedores Y borra los volúmenes (datos incluidos)
```

---

## Estructura del proyecto

```
gresk-backend/
├── src/
│   ├── main/
│   │   ├── java/com/gresk/      # Código fuente
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/migration/    # Scripts Flyway (V1__..., V2__...)
│   └── test/
├── docker-compose.yml
├── .env.example                 # Plantilla de variables (sí se sube al repo)
├── .env                         # Valores reales      (NO se sube al repo)
└── pom.xml
```
