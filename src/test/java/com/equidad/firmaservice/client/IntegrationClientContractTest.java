package com.equidad.firmaservice.client;

import com.equidad.firmaservice.config.OnBaseConfig;
import com.equidad.firmaservice.config.SignioConfig;
import com.equidad.firmaservice.dto.FirmaRequestDTO;
import com.equidad.firmaservice.exception.BusinessException;
import com.equidad.firmaservice.model.FirmaEntity;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class IntegrationClientContractTest {

    @Test
    void signioClientRealExponePendienteControlado() {
        SignioClientReal client =
                new SignioClientReal(new SignioConfig(), mock(RestClient.class));

        FirmaRequestDTO request = new FirmaRequestDTO();
        request.setIdDocumento("DOC-1");
        request.setCorreo("cliente@test.com");
        request.setPdfBase64("base64");

        assertThatThrownBy(() -> client.enviarDocumento(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("SignioClientReal pendiente de implementación");
    }

    @Test
    void onBaseClientExponePendienteControlado() {
        OnBaseClient client =
                new OnBaseClient(new OnBaseConfig(), mock(RestClient.class));

        assertThatThrownBy(() ->
                client.registrarDocumentoFirmado(new FirmaEntity()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("OnBaseClient pendiente de implementación");
    }
}
