package com.equidad.firmaservice.dto;

import java.time.LocalDateTime;

public class FirmaEventoResponseDTO {

    private String proveedor;
    private String idEventoExterno;
    private String idTransaccionExterna;
    private String documentoId;
    private String estadoExterno;
    private String estadoAnterior;
    private String estadoNuevo;
    private String mensaje;
    private LocalDateTime fechaEvento;
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
