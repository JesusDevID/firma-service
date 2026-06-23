package com.equidad.firmaservice.dto;

public class ApiResponse<T> {
    private int codigo;
    private String mensaje;
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
