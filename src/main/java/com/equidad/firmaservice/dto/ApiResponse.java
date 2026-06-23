package com.equidad.firmaservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta estándar usada por nuevos endpoints y errores controlados")
public class ApiResponse<T> {

    @Schema(description = "Código HTTP representado en el cuerpo", example = "200")
    private int codigo;

    @Schema(description = "Mensaje legible del resultado", example = "Proceso exitoso")
    private String mensaje;

    @Schema(description = "Datos de respuesta. Puede ser null en errores")
    private T data;

    public ApiResponse() {
    }

    public ApiResponse(int codigo, String mensaje, T data) {
        this.codigo = codigo;
        this.mensaje = mensaje;
        this.data = data;
    }

    public static <T> ApiResponse<T> exitoso(T data) {
        return new ApiResponse<>(200, "Proceso exitoso", data);
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
