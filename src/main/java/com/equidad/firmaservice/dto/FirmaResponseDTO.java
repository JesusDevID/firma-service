package com.equidad.firmaservice.dto;

public class FirmaResponseDTO {

    private String mensaje;
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