package com.equidad.firmaservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Solicitud para enviar un documento a firma")
public class FirmaRequestDTO {

    @NotBlank(message = "El documento es obligatorio")
    @Size(max = 100, message = "El documento no puede superar 100 caracteres")
    @Schema(
            description = "Identificador interno del documento a firmar",
            example = "DOC-123456",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String idDocumento;

    @Email(message = "Correo inválido")
    @NotBlank(message = "El correo es obligatorio")
    @Size(max = 254, message = "El correo no puede superar 254 caracteres")
    @Schema(
            description = "Correo electrónico del firmante",
            example = "cliente@test.com",
            maxLength = 254,
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String correo;

    @NotNull(message = "El PDF es obligatorio")
    @NotBlank(message = "El PDF es obligatorio")
    @Size(max = 10_000_000, message = "El PDF supera el tamaño permitido")
    @Schema(
            description = "Contenido del PDF codificado en Base64. No se registra en logs",
            example = "JVBERi0xLjQKJcTl8uXr...",
            maxLength = 10000000,
            requiredMode = Schema.RequiredMode.REQUIRED)
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
