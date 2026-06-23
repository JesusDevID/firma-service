package com.equidad.firmaservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resultado del envío de una solicitud de firma")
public class FirmaResponseDTO {

    @Schema(description = "Mensaje del procesamiento", example = "Solicitud procesada correctamente")
    private String mensaje;

    @Schema(description = "Estado interno asignado a la firma", example = "ENVIADO")
    private String estado;

    public FirmaResponseDTO() {
    }

    public FirmaResponseDTO(String mensaje,
                            String estado) {

        this.mensaje = mensaje;
        this.estado = estado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
