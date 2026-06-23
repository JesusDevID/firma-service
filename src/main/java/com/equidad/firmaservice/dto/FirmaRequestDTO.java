package com.equidad.firmaservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class FirmaRequestDTO {

    @NotBlank(message = "El documento es obligatorio")
    @Size(max = 100, message = "El documento no puede superar 100 caracteres")
    private String idDocumento;

    @Email(message = "Correo inválido")
    @NotBlank(message = "El correo es obligatorio")
    @Size(max = 254, message = "El correo no puede superar 254 caracteres")
    private String correo;

    @NotNull(message = "El PDF es obligatorio")
    @NotBlank(message = "El PDF es obligatorio")
    @Size(max = 10_000_000, message = "El PDF supera el tamaño permitido")
    private String pdfBase64;

    public String getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(String idDocumento) {
        this.idDocumento = idDocumento;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPdfBase64() {
        return pdfBase64;
    }

    public void setPdfBase64(String pdfBase64) {
        this.pdfBase64 = pdfBase64;
    }
}
