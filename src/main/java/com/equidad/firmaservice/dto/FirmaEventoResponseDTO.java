package com.equidad.firmaservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Evento externo auditado para una firma")
public class FirmaEventoResponseDTO {

    @Schema(description = "Proveedor que originó el evento", example = "SIGNIO")
    private String proveedor;

    @Schema(description = "Identificador único del evento externo", example = "SIGNIO-EVT-456")
    private String idEventoExterno;

    @Schema(description = "Identificador de transacción/sobre del proveedor", example = "SIGNIO-TX-987")
    private String idTransaccionExterna;

    @Schema(description = "Documento interno asociado", example = "DOC-123")
    private String documentoId;

    @Schema(description = "Estado recibido desde el proveedor", example = "SIGNED")
    private String estadoExterno;

    @Schema(description = "Estado interno antes de aplicar el evento", example = "ENVIADO")
    private String estadoAnterior;

    @Schema(description = "Estado interno después de aplicar el evento", example = "FIRMADO")
    private String estadoNuevo;

    @Schema(description = "Mensaje recibido o normalizado", example = "Documento firmado correctamente")
    private String mensaje;

    @Schema(description = "Fecha reportada por el proveedor", example = "2026-06-23T10:00:00")
    private LocalDateTime fechaEvento;

    @Schema(description = "Fecha en que firma-service recibió el evento", example = "2026-06-23T10:00:05")
    private LocalDateTime fechaRecepcion;

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public String getIdEventoExterno() {
        return idEventoExterno;
    }

    public void setIdEventoExterno(String idEventoExterno) {
        this.idEventoExterno = idEventoExterno;
    }

    public String getIdTransaccionExterna() {
        return idTransaccionExterna;
    }

    public void setIdTransaccionExterna(String idTransaccionExterna) {
        this.idTransaccionExterna = idTransaccionExterna;
    }

    public String getDocumentoId() {
        return documentoId;
    }

    public void setDocumentoId(String documentoId) {
        this.documentoId = documentoId;
    }

    public String getEstadoExterno() {
        return estadoExterno;
    }

    public void setEstadoExterno(String estadoExterno) {
        this.estadoExterno = estadoExterno;
    }

    public String getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(String estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public String getEstadoNuevo() {
        return estadoNuevo;
    }

    public void setEstadoNuevo(String estadoNuevo) {
        this.estadoNuevo = estadoNuevo;
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

    public LocalDateTime getFechaRecepcion() {
        return fechaRecepcion;
    }

    public void setFechaRecepcion(LocalDateTime fechaRecepcion) {
        this.fechaRecepcion = fechaRecepcion;
    }
}
