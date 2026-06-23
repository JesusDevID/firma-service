package com.equidad.firmaservice.controller;

import com.equidad.firmaservice.dto.LoginRequestDTO;
import com.equidad.firmaservice.exception.UnauthorizedException;
import com.equidad.firmaservice.security.JwtUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    @Test
    void loginConCredencialesValidasRetornaToken() {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        AuthController controller = new AuthController(jwtUtil);
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("admin");
        request.setPassword("admin123");

        when(jwtUtil.generarToken("admin")).thenReturn("token-jwt");

        var response = controller.login(request);

        assertThat(response).containsEntry("token", "token-jwt");
    }

    @Test
    void loginConCredencialesInvalidasLanzaUnauthorized() {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        AuthController controller = new AuthController(jwtUtil);
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("admin");
        request.setPassword("mal-password");

        assertThatThrownBy(() -> controller.login(request))
                .isInstanceOf(UnauthorizedException.class);
    }
}
