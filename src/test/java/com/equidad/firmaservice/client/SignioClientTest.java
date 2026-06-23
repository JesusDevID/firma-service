package com.equidad.firmaservice.client;

import com.equidad.firmaservice.config.SignioConfig;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class SignioClientTest {

    @Test
    void enviarDocumentoNoUsaRedYRetornaRespuestaMock() {
        SignioConfig config = new SignioConfig();
        ReflectionTestUtils.setField(config, "url", "https://signio.mock");
        ReflectionTestUtils.setField(config, "token", "token");

        RestClient restClient = mock(RestClient.class);
        SignioClient client = new SignioClient(config, restClient);

        var response = client.enviarDocumento("DOC-1");

        assertThat(response.getEstado()).isEqualTo("OK");
        assertThat(response.getMensaje())
                .isEqualTo("Documento enviado correctamente al mock de Signio");
        verifyNoInteractions(restClient);
    }
}
