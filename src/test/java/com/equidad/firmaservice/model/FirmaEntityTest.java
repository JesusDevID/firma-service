package com.equidad.firmaservice.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FirmaEntityTest {

    @Test
    void prePersistInicializaAuditoria() {
        FirmaEntity firma = new FirmaEntity();

        firma.prePersist();

        assertThat(firma.getFechaCreacion()).isNotNull();
        assertThat(firma.getFechaActualizacion()).isNotNull();
        assertThat(firma.getUsuarioCreacion()).isEqualTo("sistema");
        assertThat(firma.getUsuarioActualizacion()).isEqualTo("sistema");
    }

    @Test
    void preUpdateActualizaFechaYUsuarioDefault() {
        FirmaEntity firma = new FirmaEntity();

        firma.preUpdate();

        assertThat(firma.getFechaActualizacion()).isNotNull();
        assertThat(firma.getUsuarioActualizacion()).isEqualTo("sistema");
    }

    @Test
    void gettersYSettersBasicos() {
        FirmaEntity firma = new FirmaEntity();
        firma.setDocumentoId("DOC-1");
        firma.setCorreo("usuario@correo.com");
        firma.setEstado(EstadoFirma.PENDIENTE.name());
        firma.setRespuestaSignio("ok");

        assertThat(firma.getDocumentoId()).isEqualTo("DOC-1");
        assertThat(firma.getCorreo()).isEqualTo("usuario@correo.com");
        assertThat(firma.getEstado()).isEqualTo(EstadoFirma.PENDIENTE.name());
        assertThat(firma.getRespuestaSignio()).isEqualTo("ok");
    }
}
