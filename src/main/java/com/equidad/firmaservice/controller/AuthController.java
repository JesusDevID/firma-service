package com.equidad.firmaservice.controller;

import com.equidad.firmaservice.dto.LoginRequestDTO;
import com.equidad.firmaservice.exception.UnauthorizedException;
import com.equidad.firmaservice.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Operaciones de login y emisión de JWT")
public class AuthController {
    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión y obtener token JWT")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Token generado correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Solicitud inválida"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Credenciales inválidas")
    })
    public Map<String, String> login(@Valid @RequestBody LoginRequestDTO request) {
        if ("admin".equals(request.getUsername()) && "admin123".equals(request.getPassword())) {
            return Map.of("token", jwtUtil.generarToken(request.getUsername()));
        }
        throw new UnauthorizedException("Credenciales inválidas");
    }
}
