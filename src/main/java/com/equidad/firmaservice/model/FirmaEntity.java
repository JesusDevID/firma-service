package com.equidad.firmaservice.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "firmas")
public class FirmaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String documentoId;

    private String correo;

    private String estado;

    @Column(length = 5000)
    private String respuestaSignio;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;

    private String usuarioCreacion;

    private String usuarioActualizacion;

    @PrePersist
    public void prePersist() {
        LocalDateTime ahora = LocalDateTime.now();
        if (fechaCreacion == null) {
            fechaCreacion = ahora;
        }
        fechaActualizacion = ahora;
        if (usuarioCreacion == null || usuarioCreacion.isBlank()) {
            usuarioCreacion = "sistema";
        }
        if (usuarioActualizacion == null || usuarioActualizacion.isBlank()) {
            usuarioActualizacion = usuarioCreacion;
        }
    }

    @PreUpdate
    public void preUpdate() {
        fechaActualizacion = LocalDateTime.now();
        if (usuarioActualizacion == null || usuarioActualizacion.isBlank()) {
            usuarioActualizacion = "sistema";
        }
    }

    public Long getId() {
        return id;
    }

    public String getDocumentoId() {
        return documentoId;
    }

    public void setDocumentoId(String documentoId) {
        this.documentoId = documentoId;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getRespuestaSignio() {
        return respuestaSignio;
    }

    public void setRespuestaSignio(String respuestaSignio) {
        this.respuestaSignio = respuestaSignio;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public String getUsuarioCreacion() {
        return usuarioCreacion;
    }

    public void setUsuarioCreacion(String usuarioCreacion) {
        this.usuarioCreacion = usuarioCreacion;
    }

    public String getUsuarioActualizacion() {
        return usuarioActualizacion;
    }

    public void setUsuarioActualizacion(String usuarioActualizacion) {
        this.usuarioActualizacion = usuarioActualizacion;
    }
}
