package com.equidad.firmaservice.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void manejarNoEncontradoRetorna404() {
        var response = handler.manejarNoEncontrado(
                new ResourceNotFoundException("Firma no encontrada"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getCodigo()).isEqualTo(404);
    }

    @Test
    void manejarNegocioRetorna400() {
        var response = handler.manejarNegocio(
                new BusinessException("Estado inválido"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMensaje())
                .isEqualTo("Estado inválido");
    }

    @Test
    void manejarNoAutorizadoRetorna401() {
        var response = handler.manejarNoAutorizado(
                new UnauthorizedException("No autorizado"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getCodigo()).isEqualTo(401);
    }

    @Test
    void manejarErrorGeneralNoExponeDetalleInterno() {
        var response = handler.manejarErrorGeneral(
                new RuntimeException("detalle sensible"));

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getMensaje())
                .isEqualTo("Error interno del servidor");
    }
}
