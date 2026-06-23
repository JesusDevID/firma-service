# Firma Service

Microservicio Java 17 con Spring Boot 3 para la gestión de solicitudes de firma electrónica. El núcleo queda preparado para que las integraciones reales pendientes sean únicamente:

- `SignioClientReal`
- `OnBaseClient`

Actualmente Signio funciona como cliente mock local, sin llamadas HTTP externas. No hay integración real con Signio ni con OnBase.

## Funcionalidades

- Login JWT.
- Seguridad con Spring Security.
- Protección de endpoints `/firmas/**`.
- CRUD/consulta de firmas.
- Estados completos de firma.
- Envío mock a Signio.
- Validación de DTOs con Bean Validation.
- Auditoría automática de creación y actualización.
- Paginación.
- Filtros por estado, correo y rango de fechas.
- Manejo global de excepciones.
- Respuesta estándar `ApiResponse<T>` para nuevas respuestas y errores controlados.
- Swagger/OpenAPI.
- Actuator `health` e `info`.
- H2 persistente.
- Profile PostgreSQL-ready.
- Migraciones de base de datos con Flyway.
- Docker y docker-compose.
- Tests unitarios y de contexto con JaCoCo.
- CI con GitHub Actions sobre Java 17.

## Arquitectura

El proyecto mantiene la arquitectura existente:

```text
controller
service
repository
dto
model
config
security
client
exception
```

No se reestructuraron paquetes.

## Tecnologías

- Java 17
- Spring Boot 3.5.0
- Spring Web
- Spring Security
- JWT
- Spring Data JPA
- Flyway
- H2
- PostgreSQL-ready
- Bean Validation
- Swagger/OpenAPI
- Spring Boot Actuator
- JUnit 5
- Mockito
- JaCoCo
- Maven
- Docker

## Seguridad

Endpoints públicos:

```http
POST /auth/login
GET /firmas/health
GET /swagger-ui/**
GET /v3/api-docs/**
GET /actuator/health
GET /actuator/info
```

Endpoints protegidos:

```http
/firmas/**
```

Los endpoints protegidos requieren:

```http
Authorization: Bearer <jwt>
```

Si el token es inválido o está ausente, el servicio responde `401 Unauthorized`.

> Nota: `ApiKeyFilter` se conserva en el código, pero ya no se registra como filtro automático porque la seguridad principal del servicio es JWT.

## Login

```http
POST /auth/login
Content-Type: application/json
```

Body:

```json
{
  "username": "admin",
  "password": "admin123"
}
```

Respuesta:

```json
{
  "token": "jwt..."
}
```

## Endpoints de firmas

### Health check

```http
GET /firmas/health
```

### Enviar documento

```http
POST /firmas/enviar
Authorization: Bearer <jwt>
Content-Type: application/json
```

Body:

```json
{
  "idDocumento": "123456",
  "correo": "cliente@test.com",
  "pdfBase64": "PDF_TEST"
}
```

### Listar firmas

Sin paginación, compatible con el comportamiento anterior:

```http
GET /firmas
Authorization: Bearer <jwt>
```

Con paginación:

```http
GET /firmas?page=0&size=10
Authorization: Bearer <jwt>
```

### Filtros

Por estado:

```http
GET /firmas?estado=FIRMADO
Authorization: Bearer <jwt>
```

Por correo:

```http
GET /firmas?correo=cliente@test.com
Authorization: Bearer <jwt>
```

Por rango de fechas:

```http
GET /firmas?fechaInicio=2026-06-01T00:00:00&fechaFin=2026-06-30T23:59:59
Authorization: Bearer <jwt>
```

Los filtros pueden combinarse:

```http
GET /firmas?page=0&size=10&estado=FIRMADO&correo=cliente@test.com
Authorization: Bearer <jwt>
```

### Consultar firma por ID

```http
GET /firmas/{id}
Authorization: Bearer <jwt>
```

### Consultar por estado — endpoint legacy

```http
GET /firmas/estado/{estado}
Authorization: Bearer <jwt>
```

### Consultar por correo — endpoint legacy

```http
GET /firmas/correo/{correo}
Authorization: Bearer <jwt>
```

### Marcar como firmado

```http
PUT /firmas/{id}/firmar
Authorization: Bearer <jwt>
```

### Rechazar firma

```http
PUT /firmas/{id}/rechazar
Authorization: Bearer <jwt>
```

### Expirar firma

```http
PUT /firmas/{id}/expirar
Authorization: Bearer <jwt>
```

## Estados de firma

```text
PENDIENTE
ENVIADO
EN_PROCESO
FIRMADO
RECHAZADO
EXPIRADO
ERROR
```

## Respuesta estándar

Para nuevos endpoints y errores controlados se usa:

```json
{
  "codigo": 200,
  "mensaje": "Proceso exitoso",
  "data": {}
}
```

## Auditoría

`FirmaEntity` incluye:

- `fechaCreacion`
- `fechaActualizacion`
- `usuarioCreacion`
- `usuarioActualizacion`

Las fechas se inicializan automáticamente con `@PrePersist` y `@PreUpdate`.

## Actuator

Disponibles:

```http
GET /actuator/health
GET /actuator/info
```

## Swagger/OpenAPI

Disponible en:

```text
http://localhost:8080/swagger-ui.html
```

o:

```text
http://localhost:8080/swagger-ui/index.html
```

## Configuración

Archivo principal:

```text
src/main/resources/application.yaml
```

Variables externalizadas:

```text
JWT_SECRET
JWT_EXPIRATION_MILLIS
SIGNIO_URL
SIGNIO_TOKEN
```

Profiles disponibles:

- `dev`
- `qa`
- `prod`
- `postgres`
- `test`

## PostgreSQL-ready

El servicio mantiene H2 por defecto, pero incluye profile PostgreSQL:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

Variables:

```text
POSTGRES_URL
POSTGRES_USER
POSTGRES_PASSWORD
```

## Migraciones

Las migraciones Flyway están en:

```text
src/main/resources/db/migration
```

Migración inicial:

```text
V1__create_firmas_table.sql
```

En `test`, Flyway está deshabilitado y se usa H2 en memoria con `create-drop`.

## Ejecución

Compilar:

```bash
mvn clean install
```

Ejecutar:

```bash
mvn spring-boot:run
```

Con profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Tests

Ejecutar:

```bash
mvn test
```

Validar build completo y umbral mínimo de cobertura:

```bash
mvn verify
```

Suite actual:

- `FirmaServiceTest`
- `FirmaControllerTest`
- `AuthControllerTest`
- `JwtUtilTest`
- `JwtFilterTest`
- `GlobalExceptionHandlerTest`
- `FirmaEntityTest`
- `DtoValidationTest`
- `SignioClientTest`

Cobertura JaCoCo verificada: `87%`.

El build tiene umbral mínimo configurado de `80%` de cobertura de instrucciones.

Reporte:

```text
target/site/jacoco/index.html
```

El profile `test` usa H2 en memoria para evitar tocar la base persistente local durante pruebas.

## CI

El workflow de GitHub Actions está en:

```text
.github/workflows/ci.yml
```

Ejecuta:

```bash
./mvnw verify
```

con Java 17 y publica el reporte JaCoCo como artefacto.

## Docker Compose

Ejecución por defecto con H2/profile `dev`:

```bash
docker compose up --build
```

Levantar PostgreSQL auxiliar:

```bash
docker compose --profile postgres up --build
```

## Pendiente para otros equipos

El núcleo del microservicio queda preparado. Las tareas pendientes intencionales son:

- Implementar `SignioClientReal`.
- Implementar `OnBaseClient`.
