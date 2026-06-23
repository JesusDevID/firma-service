package com.equidad.firmaservice.security;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtFilterTest {

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void tokenValidoAutenticaUsuario()
            throws ServletException, IOException {

        JwtUtil jwtUtil = mock(JwtUtil.class);
        JwtFilter filter = new JwtFilter(jwtUtil);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        request.addHeader("Authorization", "Bearer token-valido");
        when(jwtUtil.validarToken("token-valido")).thenReturn(true);
        when(jwtUtil.extraerUsuario("token-valido")).thenReturn("admin");

        filter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext()
                .getAuthentication()
                .getName()).isEqualTo("admin");
    }

    @Test
    void tokenInvalidoRetorna401()
            throws ServletException, IOException {

        JwtUtil jwtUtil = mock(JwtUtil.class);
        JwtFilter filter = new JwtFilter(jwtUtil);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        request.addHeader("Authorization", "Bearer token-invalido");
        when(jwtUtil.validarToken("token-invalido"))
                .thenThrow(new RuntimeException("inválido"));

        filter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString())
                .contains("Token inválido");
    }

    @Test
    void sinHeaderContinuaCadena()
            throws ServletException, IOException {

        JwtFilter filter = new JwtFilter(mock(JwtUtil.class));
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();
    }

    @Test
    void apiKeyFilterSinHeaderRetorna401()
            throws ServletException, IOException {

        ApiKeyFilter filter = new ApiKeyFilter();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        request.setServletPath("/firmas");

        filter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    void apiKeyFilterConHeaderValidoContinua()
            throws ServletException, IOException {

        ApiKeyFilter filter = new ApiKeyFilter();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        request.setServletPath("/firmas");
        request.addHeader("x-api-key", "TOKEN_SECRETO_123");

        filter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void apiKeyFilterIgnoraHealthYSwagger() {
        ApiKeyFilter filter = new ApiKeyFilter();
        MockHttpServletRequest health = new MockHttpServletRequest();
        MockHttpServletRequest swagger = new MockHttpServletRequest();

        health.setServletPath("/firmas/health");
        swagger.setServletPath("/swagger-ui/index.html");

        assertThat(filter.shouldNotFilter(health)).isTrue();
        assertThat(filter.shouldNotFilter(swagger)).isTrue();
    }
}
