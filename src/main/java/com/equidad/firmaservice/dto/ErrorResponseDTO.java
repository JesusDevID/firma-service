package com.equidad.firmaservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Respuesta para errores internos no controlados")
public class ErrorResponseDTO {

    @Schema(description = "Mensaje seguro para el consumidor", example = "Error interno del servidor")
    private String mensaje;

    @Schema(description = "Código HTTP", example = "500")
    private int status;

    @Schema(description = "Fecha del error", example = "2026-06-23T10:00:00")
    private LocalDateTime fecha;

    public ErrorResponseDTO() {
    }

    public ErrorResponseDTO(
            String mensaje,
            int status,
            LocalDateTime fecha) {

        this.mensaje = mensaje;
        this.status = status;
        this.fecha = fecha;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
