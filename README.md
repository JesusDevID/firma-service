# Firma Service

Microservicio desarrollado en Spring Boot para la gestión de solicitudes de firma electrónica e integración con Signio.

## Descripción

Este servicio permite:

- Enviar documentos para firma electrónica.
- Consultar solicitudes registradas.
- Consultar firmas por ID.
- Consultar firmas por estado.
- Consultar firmas por correo electrónico.
- Marcar documentos como firmados.
- Persistir información en base de datos mediante JPA.

## Arquitectura

El proyecto sigue una arquitectura por capas:

```
Controller
    ↓
Service
    ↓
Repository
    ↓
Base de Datos
```

### Componentes

- controller
    - Exposición de endpoints REST.

- service
    - Lógica de negocio.

- repository
    - Acceso a datos mediante Spring Data JPA.

- dto
    - Objetos de transferencia de datos.

- model
    - Entidades del sistema.

- client
    - Integración con servicios externos (Signio).

- security
    - Seguridad mediante API Key.

- exception
    - Manejo global de excepciones.

---

## Tecnologías

- Java 17
- Spring Boot
- Spring Data JPA
- H2 Database
- Swagger / OpenAPI
- Maven

---

## Configuración

Archivo:

```yaml
src/main/resources/application.yaml
```

Ejemplo:

```yaml
server:
  port: 8080

signio:
  url: https://api.signio.com
  token: TOKEN_PRUEBA_123
```

---

## Ejecución

Compilar:

```bash
mvn clean install
```

Ejecutar:

```bash
mvn spring-boot:run
```

O ejecutar la clase:
 
````md
```java
FirmaServiceApplication
```

---

## Swagger

Disponible en:

```
http://localhost:8080/swagger-ui.html
```

o

```
http://localhost:8080/swagger-ui/index.html
```

---

## Endpoints

### Health Check

```http
GET /firmas/health
```

---

### Enviar Documento

```http
POST /firmas/enviar
```

Body:

```json
{
  "idDocumento": "123456",
  "correo": "cliente@test.com",
  "pdfBase64": "PDF_TEST"
}
```

---

### Obtener Todas las Firmas

```http
GET /firmas
```

---

### Obtener Firma por ID

```http
GET /firmas/{id}
```

Ejemplo:

```http
GET /firmas/1
```

---

### Obtener Firmas por Estado

```http
GET /firmas/estado/{estado}
```

Ejemplo:

```http
GET /firmas/estado/ENVIADO
```

---

### Obtener Firmas por Correo

```http
GET /firmas/correo/{correo}
```

Ejemplo:

```http
GET /firmas/correo/cliente@test.com
```

---

### Marcar Documento como Firmado

```http
PUT /firmas/{id}/firmar
```

Ejemplo:

```http
PUT /firmas/1/firmar
```

---

## Estados

El sistema actualmente maneja:

- ENVIADO
- FIRMADO
- RECHAZADO

---

## Seguridad

El servicio utiliza validación mediante API Key en los encabezados HTTP.

Ejemplo:

```http
x-api-key: TOKEN_SECRETO_123
```

---

## Próximas Integraciones

- Signio (firma electrónica real)
- OnBase
- PostgreSQL
- Docker
- JWT
- Pruebas unitarias