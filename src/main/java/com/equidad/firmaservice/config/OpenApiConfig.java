package com.equidad.firmaservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Firma Service API",
                version = "0.0.1-SNAPSHOT",
                description = """
                        API empresarial para gestionar solicitudes de firma electrónica.

                        Flujo recomendado:
                        1. Autenticarse en /auth/login.
                        2. Usar el token JWT con el botón Authorize.
                        3. Crear solicitudes de firma en /firmas/enviar.
                        4. Consultar estado, filtros e historial.
                        5. Recibir eventos externos por /webhooks/signio.

                        Signio y OnBase reales están preparados mediante contratos,
                        pero permanecen en modo mock/pendiente hasta configurar las
                        integraciones reales.
                        """,
                contact = @Contact(
                        name = "Equipo firma-service",
                        email = "soporte@equidad.local"),
                license = @License(
                        name = "Uso interno")
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Local / Docker"),
                @Server(
                        url = "https://api.example.com",
                        description = "Ambiente real configurable")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER,
        description = "JWT obtenido desde /auth/login. Formato: Bearer <token>"
)
public class OpenApiConfig {
}
