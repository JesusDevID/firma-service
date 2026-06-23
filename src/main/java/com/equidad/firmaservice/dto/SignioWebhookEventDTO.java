package com.equidad.firmaservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Schema(description = "Evento normalizado recibido desde el webhook de Signio")
public class SignioWebhookEventDTO {

    @NotBlank(message = "El documento es obligatorio")
    @Size(max = 100, message = "El documento no puede superar 100 caracteres")
    @Schema(
            description = "Identificador interno del documento enviado a firma",
            example = "DOC-123")
    private String documentoId;

    @NotBlank(message = "El estado es obligatorio")
    @Size(max = 50, message = "El estado no puede superar 50 caracteres")
    @Schema(
            description = "Estado externo recibido desde Signio. Se normaliza a EstadoFirma",
            example = "SIGNED")
    private String estado;

    @Size(max = 255, message = "La transacción externa no puede superar 255 caracteres")
    @Schema(
            description = "Identificador de transacción o sobre devuelto por Signio",
            example = "SIGNIO-TX-987")
    private String idTransaccionExterna;

    @Size(max = 500, message = "El mensaje no puede superar 500 caracteres")
    @Schema(
            description = "Mensaje descriptivo del evento externo",
            example = "Documento firmado correctamente")
    private String mensaje;

    @Schema(
            description = "Fecha y hora del evento reportada por Signio",
            example = "2026-06-23T10:00:00")
    private LocalDateTime fechaEvento;

    public String getDocumentoId() {
        return documentoId;
    }

    public void setDocumentoId(String documentoId) {
        this.documentoId = documentoId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getIdTransaccionExterna() {
        return idTransaccionExterna;
    }

    public void setIdTransaccionExterna(String idTransaccionExterna) {
        this.idTransaccionExterna = idTransaccionExterna;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(LocalDateTime fechaEvento) {
        this.fechaEvento = fechaEvento;
    }
}
