package com.equidad.firmaservice.client;

import com.equidad.firmaservice.config.SignioConfig;
import com.equidad.firmaservice.dto.FirmaRequestDTO;
import com.equidad.firmaservice.dto.SignioResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@ConditionalOnProperty(
        name = "integrations.signio.mode",
        havingValue = "mock",
        matchIfMissing = true)
public class SignioClient implements FirmaProviderClient {

    private static final Logger logger =
            LoggerFactory.getLogger(SignioClient.class);

    private final SignioConfig signioConfig;
    private final RestClient restClient;

    public SignioClient(SignioConfig signioConfig,
                        RestClient restClient) {

        this.signioConfig = signioConfig;
        this.restClient = restClient;
    }

    @Override
    public SignioResponseDTO enviarDocumento(FirmaRequestDTO request) {

        logger.info("Preparando envío mock a Signio");

        SignioResponseDTO responseDTO =
                new SignioResponseDTO();

        responseDTO.setEstado("OK");
        responseDTO.setMensaje(
                "Documento enviado correctamente al mock de Signio"
        );

        logger.info("Respuesta mock de Signio generada");

        return responseDTO;
    }

    public SignioResponseDTO enviarDocumento(String documentoId) {
        FirmaRequestDTO request = new FirmaRequestDTO();
        request.setIdDocumento(documentoId);
        return enviarDocumento(request);
    }
}
