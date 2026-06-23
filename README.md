# Firma Service

Microservicio Java 17 con Spring Boot 3 para la gestión de solicitudes de firma electrónica. El núcleo queda preparado para que las integraciones reales pendientes sean únicamente:

- `SignioClientReal`
- `OnBaseClient`

Actualmente Signio funciona como cliente mock local, sin llamadas HTTP externas. No hay integración real con Signio ni con OnBase, pero el servicio ya incluye un endpoint webhook preparado para recibir eventos normalizados de Signio.

## Funcionalidades

- Login JWT.
- Seguridad con Spring Security.
- Protección de endpoints `/firmas/**`.
- CRUD/consulta de firmas.
- Estados completos de firma.
- Envío mock a Signio.
- Webhook preparado para eventos de Signio.
- Idempotencia de webhooks por `idEventoExterno`.
- Historial auditable de eventos externos de firma.
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
POST /webhooks/signio
GET /swagger-ui/**
GET /v3/api-docs/**
GET /actuator/health
GET /actuator/health/liveness
GET /actuator/health/readiness
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

### Consultar historial de eventos

```http
GET /firmas/{id}/eventos
Authorization: Bearer <jwt>
```

Devuelve los eventos externos recibidos para una firma, sin exponer payloads sensibles ni detalles internos del proveedor.

## Webhook de Signio

Endpoint preparado:

```http
POST /webhooks/signio
Content-Type: application/json
X-Signio-Webhook-Secret: <SIGNIO_WEBHOOK_SECRET>
```

Este endpoint no requiere JWT porque será invocado por Signio. La protección prevista es el secreto compartido `X-Signio-Webhook-Secret`.

Body normalizado actual:

```json
{
  "documentoId": "DOC-123",
  "estado": "SIGNED",
  "idEventoExterno": "SIGNIO-EVT-456",
  "idTransaccionExterna": "SIGNIO-TX-987",
  "mensaje": "Documento firmado correctamente",
  "fechaEvento": "2026-06-23T10:00:00"
}
```

Respuesta:

```json
{
  "codigo": 200,
  "mensaje": "Proceso exitoso",
  "data": {
    "documentoId": "DOC-123",
    "estado": "FIRMADO",
    "proveedorFirma": "SIGNIO",
    "ultimoEventoExterno": "SIGNED",
    "idTransaccionExterna": "SIGNIO-TX-987",
    "fechaUltimoEvento": "2026-06-23T10:00:00"
  }
}
```

### Idempotencia de webhooks

Si Signio reintenta el mismo evento con el mismo `idEventoExterno`, el servicio lo detecta y no reprocesa el cambio de estado. Esto evita cambios repetidos, duplicidad en el historial y efectos secundarios cuando el proveedor haga reintentos.

### Historial auditable

Cada evento nuevo recibido se almacena en `firma_eventos` con:

- proveedor
- id del evento externo
- id de transacción externa
- documento
- estado externo
- estado anterior
- estado nuevo
- mensaje
- fecha del evento
- fecha de recepción

Mapeo de estados externos soportado:

```text
PENDING / PENDIENTE -> PENDIENTE
SENT / ENVIADO -> ENVIADO
PROCESSING / IN_PROCESS / EN_PROCESO -> EN_PROCESO
SIGNED / COMPLETED / FIRMADO -> FIRMADO
REJECTED / DECLINED / RECHAZADO -> RECHAZADO
EXPIRED / EXPIRADO -> EXPIRADO
ERROR / FAILED -> ERROR
```

> Nota: cuando se confirme el payload real de Signio, solo debería ajustarse el DTO/adaptador de entrada o el mapeo, no el núcleo del servicio.

## Modos de integración

Por defecto el servicio trabaja en modo seguro/mock:

```yaml
integrations:
  signio:
    mode: mock
  onbase:
    enabled: false
```

Para activar el cliente real de Signio cuando esté implementado:

```env
SIGNIO_INTEGRATION_MODE=real
```

Para activar OnBase cuando esté implementado:

```env
ONBASE_ENABLED=true
```

El handoff técnico completo está en:

```text
docs/INTEGRACIONES.md
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
- `proveedorFirma`
- `idTransaccionExterna`
- `ultimoEventoExterno`
- `fechaUltimoEvento`

Las fechas se inicializan automáticamente con `@PrePersist` y `@PreUpdate`.

Los campos externos permiten rastrear el proveedor de firma y los eventos recibidos por webhook sin acoplar el dominio al payload real de Signio.

## Actuator

Disponibles:

```http
GET /actuator/health
GET /actuator/health/liveness
GET /actuator/health/readiness
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

JSON OpenAPI:

```text
http://localhost:8080/v3/api-docs
```

Uso recomendado desde Swagger UI:

1. Ejecutar `POST /auth/login` con las credenciales locales de desarrollo.
2. Copiar el valor `token` de la respuesta.
3. Presionar el botón `Authorize` y pegar el JWT como Bearer token.
4. Probar los endpoints de `Firmas`.
5. Para pruebas de webhook, usar `POST /webhooks/signio` con el header `X-Signio-Webhook-Secret`.

La documentación OpenAPI incluye:

- descripción del flujo funcional del servicio;
- tags por módulo: autenticación, firmas y webhooks Signio;
- seguridad JWT Bearer documentada;
- ejemplos de request/response en los DTO principales;
- descripción de filtros, paginación y cambios de estado;
- contrato del webhook preparado para la futura integración real con Signio.

## Configuración

Archivo principal:

```text
src/main/resources/application.yaml
```

Plantilla de variables:

```text
.env.example
```

Guía de handoff para implementar Signio y OnBase reales:

```text
docs/INTEGRACIONES.md
```

Variables externalizadas:

```text
JWT_SECRET
JWT_EXPIRATION_MILLIS
SIGNIO_URL
SIGNIO_TOKEN
SIGNIO_WEBHOOK_SECRET
SIGNIO_WEBHOOK_URL
SIGNIO_INTEGRATION_MODE
ONBASE_URL
ONBASE_TOKEN
ONBASE_ENABLED
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
V2__add_external_integration_fields.sql
V3__create_firma_eventos_table.sql
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
- `SignioWebhookControllerTest`
- `AuthControllerTest`
- `JwtUtilTest`
- `JwtFilterTest`
- `SecurityIntegrationTest`
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
- Confirmar URL pública del webhook de Signio (`SIGNIO_WEBHOOK_URL`).
- Confirmar secreto/header de seguridad de webhook (`SIGNIO_WEBHOOK_SECRET` o esquema HMAC real).
- Ajustar el adaptador si el payload real de Signio difiere del contrato normalizado actual.
- Sustituir la idempotencia por la firma/campo oficial del proveedor si Signio usa un identificador distinto.
