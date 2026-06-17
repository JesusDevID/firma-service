package com.equidad.firmaservice.exception;

import com.equidad.firmaservice.dto.ErrorResponseDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> manejarValidaciones(
            MethodArgumentNotValidException ex) {

        String mensaje = ex.getBindingResult()
                .getFieldError() != null
                ? ex.getBindingResult()
                .getFieldError()
                .getDefaultMessage()
                : "Error de validación";

        logger.error("Error de validación: {}", mensaje);

        ErrorResponseDTO error =
                new ErrorResponseDTO();

        error.setMensaje(mensaje);
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setFecha(LocalDateTime.now());

        return ResponseEntity
                .badRequest()
                .body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> manejarErrorGeneral(
            Exception ex) {

        logger.error("Error interno: {}", ex.getMessage());

        ErrorResponseDTO error =
                new ErrorResponseDTO();

        error.setMensaje(ex.getMessage());
        error.setStatus(
                HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setFecha(LocalDateTime.now());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}
