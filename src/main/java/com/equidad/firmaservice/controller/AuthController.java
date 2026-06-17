package com.equidad.firmaservice.controller;

import com.equidad.firmaservice.dto.LoginRequestDTO;
import com.equidad.firmaservice.security.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public Map<String, String> login(
            @RequestBody LoginRequestDTO request) {

        if ("admin".equals(request.getUsername())
                && "admin123".equals(request.getPassword())) {

            String token =
                    jwtUtil.generarToken(request.getUsername());

            return Map.of("token", token);
        }

        throw new RuntimeException("Credenciales inválidas");
    }
}