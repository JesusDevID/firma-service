package com.equidad.firmaservice.controller;

import com.equidad.firmaservice.config.SignioConfig;
import com.equidad.firmaservice.dto.SignioWebhookEventDTO;
import com.equidad.firmaservice.exception.UnauthorizedException;
import com.equidad.firmaservice.model.EstadoFirma;
import com.equidad.firmaservice.model.FirmaEntity;
import com.equidad.firmaservice.service.FirmaService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SignioWebhookControllerTest {

    @Test
    void recibirEventoValidaSecretoYRetornaApiResponse() {
        FirmaService service = mock(FirmaService.class);
        SignioConfig config = new SignioConfig();
        ReflectionTestUtils.setField(config, "webhookSecret", "secret-test");
        SignioWebhookController controller =
                new SignioWebhookController(service, config);

        SignioWebhookEventDTO event = new SignioWebhookEventDTO();
        event.setDocumentoId("DOC-1");
        event.setEstado("SIGNED");

        FirmaEntity firma = new FirmaEntity();
        firma.setEstado(EstadoFirma.FIRMADO.name());

        when(service.procesarEventoSignio(event)).thenReturn(firma);

        var response = controller.recibirEvento("secret-test", event);

        assertThat(response.getCodigo()).isEqualTo(200);
        assertThat(response.getData().getEstado())
                .isEqualTo(EstadoFirma.FIRMADO.name());
    }

    @Test
    void recibirEventoConSecretoInvalidoLanzaUnauthorized() {
        FirmaService service = mock(FirmaService.class);
        SignioConfig config = new SignioConfig();
        ReflectionTestUtils.setField(config, "webhookSecret", "secret-test");
        SignioWebhookController controller =
                new SignioWebhookController(service, config);

        SignioWebhookEventDTO event = new SignioWebhookEventDTO();
        event.setDocumentoId("DOC-1");
        event.setEstado("SIGNED");

        assertThatThrownBy(() ->
                controller.recibirEvento("otro-secret", event))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Secreto de webhook inválido");
    }
}
