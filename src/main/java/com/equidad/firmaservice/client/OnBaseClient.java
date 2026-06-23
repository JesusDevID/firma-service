package com.equidad.firmaservice.client;

import com.equidad.firmaservice.config.OnBaseConfig;
import com.equidad.firmaservice.exception.BusinessException;
import com.equidad.firmaservice.model.FirmaEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@ConditionalOnProperty(
        name = "integrations.onbase.enabled",
        havingValue = "true")
public class OnBaseClient implements DocumentStorageClient {

    private final OnBaseConfig onBaseConfig;
    private final RestClient restClient;

    public OnBaseClient(OnBaseConfig onBaseConfig,
                        RestClient restClient) {
        this.onBaseConfig = onBaseConfig;
        this.restClient = restClient;
    }

    @Override
    public void registrarDocumentoFirmado(FirmaEntity firma) {

        /*
         * TODO OnBase real:
         * 1. Confirmar endpoint de carga/actualización documental.
         * 2. Definir si se envía PDF firmado, metadata o ambos.
         * 3. Enviar Authorization Bearer onBaseConfig.getToken().
         * 4. Mapear errores de OnBase a BusinessException o reintentos.
         * 5. Guardar identificador externo de OnBase si el negocio lo pide.
         */

        throw new BusinessException(
                "OnBaseClient pendiente de implementación. Configure integrations.onbase.enabled=false hasta completarlo.");
    }
}
