package com.equidad.firmaservice.controller;

import com.equidad.firmaservice.config.SignioConfig;
import com.equidad.firmaservice.dto.ApiResponse;
import com.equidad.firmaservice.dto.SignioWebhookEventDTO;
import com.equidad.firmaservice.exception.UnauthorizedException;
import com.equidad.firmaservice.model.FirmaEntity;
import com.equidad.firmaservice.service.FirmaService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhooks/signio")
@Tag(name = "Webhooks Signio", description = "Recepción de eventos externos de Signio")
public class SignioWebhookController {

    private static final Logger logger =
            LoggerFactory.getLogger(SignioWebhookController.class);

    private final FirmaService firmaService;
    private final SignioConfig signioConfig;

    public SignioWebhookController(FirmaService firmaService,
                                   SignioConfig signioConfig) {
        this.firmaService = firmaService;
        this.signioConfig = signioConfig;
    }

    @PostMapping
    @Operation(
            summary = "Recibir evento webhook de Signio",
            description = "Endpoint preparado para eventos de Signio. Actualmente usa contrato estable interno hasta confirmar el payload real del proveedor.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Evento procesado correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Evento inválido o estado externo no soportado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Secreto de webhook inválido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Firma no encontrada para el documento informado")
    })
    public ApiResponse<FirmaEntity> recibirEvento(
            @Parameter(description = "Secreto compartido configurado en SIGNIO_WEBHOOK_SECRET")
            @RequestHeader(value = "X-Signio-Webhook-Secret", required = false)
            String webhookSecret,
            @Valid @RequestBody SignioWebhookEventDTO event) {

        validarSecreto(webhookSecret);

        logger.info("Webhook Signio recibido para documentoId={}",
                event.getDocumentoId());

        return ApiResponse.exitoso(
                firmaService.procesarEventoSignio(event));
    }

    private void validarSecreto(String webhookSecret) {

        String configuredSecret = signioConfig.getWebhookSecret();

        if (configuredSecret == null || configuredSecret.isBlank()) {
            logger.warn("Webhook Signio sin secreto configurado; usar solo en ambientes no productivos");
            return;
        }

        if (!configuredSecret.equals(webhookSecret)) {
            throw new UnauthorizedException("Secreto de webhook inválido");
        }
    }
}
