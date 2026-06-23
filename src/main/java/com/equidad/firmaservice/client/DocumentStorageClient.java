package com.equidad.firmaservice.client;

import com.equidad.firmaservice.model.FirmaEntity;

public interface DocumentStorageClient {

    void registrarDocumentoFirmado(FirmaEntity firma);
}
