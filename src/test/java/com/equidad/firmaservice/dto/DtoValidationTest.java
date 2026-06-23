package com.equidad.firmaservice.dto;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DtoValidationTest {

    @Test
    void firmaRequestInvalidoReportaErrores() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            FirmaRequestDTO request = new FirmaRequestDTO();
            request.setIdDocumento("");
            request.setCorreo("correo-invalido");
            request.setPdfBase64("");

            var violations = validator.validate(request);

            assertThat(violations).hasSizeGreaterThanOrEqualTo(3);
        }
    }

    @Test
    void loginRequestInvalidoReportaErrores() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            LoginRequestDTO request = new LoginRequestDTO();
            request.setUsername("");
            request.setPassword("123");

            var violations = validator.validate(request);

            assertThat(violations).hasSizeGreaterThanOrEqualTo(2);
        }
    }

    @Test
    void apiResponseExitosoContieneCodigoMensajeYData() {
        ApiResponse<String> response = ApiResponse.exitoso("ok");

        assertThat(response.getCodigo()).isEqualTo(200);
        assertThat(response.getMensaje()).isEqualTo("Proceso exitoso");
        assertThat(response.getData()).isEqualTo("ok");
    }

    @Test
    void firmaResponseGettersYSetters() {
        FirmaResponseDTO response =
                new FirmaResponseDTO("mensaje", "ENVIADO");

        response.setMensaje("otro mensaje");
        response.setEstado("FIRMADO");

        assertThat(response.getMensaje()).isEqualTo("otro mensaje");
        assertThat(response.getEstado()).isEqualTo("FIRMADO");
    }

    @Test
    void signioResponseGettersYSetters() {
        SignioResponseDTO response = new SignioResponseDTO();

        response.setEstado("OK");
        response.setMensaje("mensaje");

        assertThat(response.getEstado()).isEqualTo("OK");
        assertThat(response.getMensaje()).isEqualTo("mensaje");
    }

    @Test
    void errorResponseGettersYSettersYConstructor() {
        LocalDateTime fecha = LocalDateTime.now();
        ErrorResponseDTO response =
                new ErrorResponseDTO("error", 400, fecha);

        response.setMensaje("otro error");
        response.setStatus(500);
        response.setFecha(fecha.plusDays(1));

        assertThat(response.getMensaje()).isEqualTo("otro error");
        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getFecha()).isEqualTo(fecha.plusDays(1));
    }
}
