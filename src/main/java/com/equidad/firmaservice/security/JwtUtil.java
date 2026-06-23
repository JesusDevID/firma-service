package com.equidad.firmaservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    private final SecretKey key;
    private final long expirationMillis;

    public JwtUtil(
            @Value("${security.jwt.secret:equidad-firma-service-jwt-secret-key-2026-super-segura-y-mas-larga}") String secret,
            @Value("${security.jwt.expiration-millis:86400000}") long expirationMillis) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = expirationMillis;
    }

    public String generarToken(String usuario) {
        Date ahora = new Date();
        return Jwts.builder()
                .setSubject(usuario)
                .setIssuedAt(ahora)
                .setExpiration(new Date(ahora.getTime() + expirationMillis))
                .signWith(key)
                .compact();
    }

    public String extraerUsuario(String token) {
        return extraerClaims(token).getSubject();
    }

    public boolean validarToken(String token) {
        Claims claims = extraerClaims(token);
        return claims.getSubject() != null && claims.getExpiration().after(new Date());
    }

    private Claims extraerClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
