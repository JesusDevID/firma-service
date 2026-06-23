package com.equidad.firmaservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "firma_eventos",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_firma_evento_proveedor_evento",
                        columnNames = {"proveedor", "id_evento_externo"})
        })
public class FirmaEventoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "firma_id")
    private FirmaEntity firma;

    private String proveedor;

    private String idEventoExterno;

    private String idTransaccionExterna;

    private String documentoId;

    private String estadoExterno;

    private String estadoAnterior;

    private String estadoNuevo;

    @Column(length = 1000)
    private String mensaje;

    private LocalDateTime fechaEvento;

    private LocalDateTime fechaRecepcion;

    @PrePersist
    public void prePersist() {
        if (fechaRecepcion == null) {
            fechaRecepcion = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public FirmaEntity getFirma() {
        return firma;
    }

    public void setFirma(FirmaEntity firma) {
        this.firma = firma;
    }

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
