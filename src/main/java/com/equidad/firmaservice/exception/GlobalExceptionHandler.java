package com.equidad.firmaservice.exception;

import com.equidad.firmaservice.dto.ApiResponse;
import com.equidad.firmaservice.dto.ErrorResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> manejarNoEncontrado(
            ResourceNotFoundException ex) {

        logger.warn("Recurso no encontrado: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(
                        HttpStatus.NOT_FOUND.value(),
                        ex.getMessage(),
                        null));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> manejarNegocio(
            BusinessException ex) {

        logger.warn("Regla de negocio: {}", ex.getMessage());

        return ResponseEntity
                .badRequest()
                .body(new ApiResponse<>(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage(),
                        null));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Object>> manejarNoAutorizado(
            UnauthorizedException ex) {

        logger.warn("Solicitud no autorizada");

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(
                        HttpStatus.UNAUTHORIZED.value(),
                        ex.getMessage(),
                        null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> manejarValidaciones(
            MethodArgumentNotValidException ex) {

        String mensaje = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField()
                        + ": "
                        + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));

        if (mensaje.isBlank()) {
            mensaje = "Error de validación";
        }

        logger.warn("Error de validación: {}", mensaje);

        return ResponseEntity
                .badRequest()
                .body(new ApiResponse<>(
                        HttpStatus.BAD_REQUEST.value(),
                        mensaje,
                        null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> manejarErrorGeneral(
            Exception ex) {

        logger.error("Error interno no controlado", ex);

        ErrorResponseDTO error =
                new ErrorResponseDTO();

        error.setMensaje("Error interno del servidor");
        error.setStatus(
                HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setFecha(LocalDateTime.now());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}
