package com.equidad.firmaservice.client;

import com.equidad.firmaservice.config.SignioConfig;
import com.equidad.firmaservice.dto.FirmaRequestDTO;
import com.equidad.firmaservice.dto.SignioResponseDTO;
import com.equidad.firmaservice.exception.BusinessException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@ConditionalOnProperty(
        name = "integrations.signio.mode",
        havingValue = "real")
public class SignioClientReal implements FirmaProviderClient {

    private final SignioConfig signioConfig;
    private final RestClient restClient;

    public SignioClientReal(SignioConfig signioConfig,
                            RestClient restClient) {
        this.signioConfig = signioConfig;
        this.restClient = restClient;
    }

    @Override
    public SignioResponseDTO enviarDocumento(FirmaRequestDTO request) {

        /*
         * TODO Signio real:
         * 1. Confirmar endpoint final con Signio.
         * 2. Construir payload usando request.getIdDocumento(),
         *    request.getCorreo() y request.getPdfBase64().
         * 3. Enviar Authorization Bearer signioConfig.getToken().
         * 4. Mapear respuesta externa a SignioResponseDTO.
         * 5. Devolver estado "OK" si Signio aceptó la solicitud.
         * 6. Guardar idTransaccionExterna si Signio lo retorna y el contrato
         *    real lo requiere en el core.
         */

        throw new BusinessException(
                "SignioClientReal pendiente de implementación. Configure integrations.signio.mode=mock hasta completarlo.");
    }
}
