# Guía de integración: Signio y OnBase

Esta guía está pensada para que el siguiente ingeniero implemente las integraciones reales sin tocar el núcleo del microservicio.

## Objetivo

El core ya está preparado. Las únicas piezas que deberían completarse son:

- `SignioClientReal`
- `OnBaseClient`

No debería ser necesario modificar:

- controllers existentes,
- repositorios principales,
- entidades de firma,
- seguridad JWT,
- manejo global de errores,
- estados internos,
- paginación/filtros,
- auditoría base.

## Modo actual

Por defecto el servicio corre en modo mock:

```yaml
integrations:
  signio:
    mode: mock
  onbase:
    enabled: false
```

Esto mantiene el microservicio estable en local, CI y ambientes sin credenciales reales.

## Contratos disponibles

### FirmaProviderClient

Archivo:

```text
src/main/java/com/equidad/firmaservice/client/FirmaProviderClient.java
```

Contrato:

```java
SignioResponseDTO enviarDocumento(FirmaRequestDTO request);
```

Implementaciones:

- `SignioClient`: mock actual.
- `SignioClientReal`: plantilla real pendiente.

### DocumentStorageClient

Archivo:

```text
src/main/java/com/equidad/firmaservice/client/DocumentStorageClient.java
```

Contrato:

```java
void registrarDocumentoFirmado(FirmaEntity firma);
```

Implementación pendiente:

- `OnBaseClient`

## Integrar Signio real

Archivo a completar:

```text
src/main/java/com/equidad/firmaservice/client/SignioClientReal.java
```

Pasos sugeridos:

1. Confirmar URL base y endpoint final de creación/envío de firma.
2. Confirmar esquema de autenticación:
   - Bearer token,
   - API key,
   - OAuth,
   - otro.
3. Construir payload con:
   - `request.getIdDocumento()`,
   - `request.getCorreo()`,
   - `request.getPdfBase64()`.
4. Enviar la solicitud con `RestClient`.
5. Mapear la respuesta externa a `SignioResponseDTO`.
6. Devolver `estado = "OK"` cuando Signio acepte la solicitud.
7. Si Signio retorna un identificador externo, mapearlo al flujo donde corresponda.
8. Mantener logs sin imprimir token, PDF base64 ni datos sensibles.

Activar implementación real:

```env
SIGNIO_INTEGRATION_MODE=real
SIGNIO_URL=https://...
SIGNIO_TOKEN=...
```

Volver a mock:

```env
SIGNIO_INTEGRATION_MODE=mock
```

## Integrar webhook real de Signio

Endpoint ya disponible:

```http
POST /webhooks/signio
```

Archivo principal:

```text
src/main/java/com/equidad/firmaservice/controller/SignioWebhookController.java
```

DTO normalizado:

```text
src/main/java/com/equidad/firmaservice/dto/SignioWebhookEventDTO.java
```

Payload normalizado actual:

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

Si el payload real de Signio es distinto, preferir crear un adaptador/DTO de entrada y convertirlo a `SignioWebhookEventDTO`, sin cambiar el core de estados.

Seguridad actual:

```http
X-Signio-Webhook-Secret: <SIGNIO_WEBHOOK_SECRET>
```

Si Signio usa HMAC:

1. Leer el body raw.
2. Validar firma con el secreto compartido.
3. Rechazar con `401` si no coincide.
4. Mantener `SignioWebhookEventDTO` como contrato interno normalizado.

## Integrar OnBase real

Archivo a completar:

```text
src/main/java/com/equidad/firmaservice/client/OnBaseClient.java
```

Pasos sugeridos:

1. Confirmar si OnBase recibirá:
   - PDF firmado,
   - metadata,
   - ambos.
2. Confirmar endpoint final y autenticación.
3. Implementar `registrarDocumentoFirmado(FirmaEntity firma)`.
4. Definir si se invoca después de recibir `FIRMADO` por webhook.
5. Mapear errores controlados a `BusinessException`.
6. Evaluar reintentos si OnBase está temporalmente no disponible.

Activar OnBase:

```env
ONBASE_ENABLED=true
ONBASE_URL=https://...
ONBASE_TOKEN=...
```

Mientras no esté implementado:

```env
ONBASE_ENABLED=false
```

## Tests recomendados al completar integración

Ejecutar siempre:

```bash
./mvnw verify
```

Agregar o adaptar:

- test de cliente Signio real con mock server,
- test de webhook con payload real,
- test de HMAC/header real,
- test de OnBase con mock server,
- test de error externo controlado.

## Checklist final

- [ ] `SIGNIO_INTEGRATION_MODE=real` probado en QA.
- [ ] Webhook real recibido y mapeado.
- [ ] Idempotencia validada con evento repetido.
- [ ] `ONBASE_ENABLED=true` probado en QA.
- [ ] No se imprimen tokens/PDF base64 en logs.
- [ ] `mvn verify` pasa.
- [ ] Swagger/README actualizados si cambió el contrato externo.
