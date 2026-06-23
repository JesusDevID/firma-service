package com.equidad.firmaservice.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil(
            "equidad-firma-service-jwt-secret-key-test-2026-super-segura",
            86400000);

    @Test
    void generarYValidarToken() {
        String token = jwtUtil.generarToken("admin");

        assertThat(jwtUtil.validarToken(token)).isTrue();
        assertThat(jwtUtil.extraerUsuario(token)).isEqualTo("admin");
    }

    @Test
    void validarTokenInvalidoLanzaExcepcion() {
        assertThatThrownBy(() -> jwtUtil.validarToken("token-invalido"))
                .isInstanceOf(RuntimeException.class);
    }
}
