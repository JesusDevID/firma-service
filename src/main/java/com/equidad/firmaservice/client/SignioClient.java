package com.equidad.firmaservice.client;

import com.equidad.firmaservice.config.SignioConfig;
import com.equidad.firmaservice.dto.SignioResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class SignioClient {

    private static final Logger logger =
            LoggerFactory.getLogger(SignioClient.class);

    private final SignioConfig signioConfig;
    private final RestClient restClient;

    public SignioClient(SignioConfig signioConfig,
                        RestClient restClient) {

        this.signioConfig = signioConfig;
        this.restClient = restClient;
    }

    public SignioResponseDTO enviarDocumento(String documentoId) {

        logger.info("Enviando documento a Signio: {}", documentoId);

        logger.info("URL Signio: {}",
                signioConfig.getUrl());

        Map<String, String> body = Map.of(
                "documentoId", documentoId
        );

        restClient.post()
                .uri("https://httpbin.org/post")
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + signioConfig.getToken()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);

        logger.info("Respuesta HTTP recibida");

        SignioResponseDTO responseDTO =
                new SignioResponseDTO();

        responseDTO.setEstado("OK");

        // RESPUESTA LIMPIA
        responseDTO.setMensaje(
                "Documento enviado correctamente a Signio"
        );

        return responseDTO;
    }
}

