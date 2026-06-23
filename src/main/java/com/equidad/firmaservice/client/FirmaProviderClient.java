package com.equidad.firmaservice.client;

import com.equidad.firmaservice.dto.FirmaRequestDTO;
import com.equidad.firmaservice.dto.SignioResponseDTO;

public interface FirmaProviderClient {

    SignioResponseDTO enviarDocumento(FirmaRequestDTO request);
}
