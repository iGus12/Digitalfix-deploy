package com.digitalfix.ms_rabbit_admin.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validarDTO(
            MethodArgumentNotValidException ex) {

        String errores = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error ->
                error.getField() + ": " + error.getDefaultMessage()
            )
            .collect(Collectors.joining("; "));

        return ResponseEntity.badRequest().body(Map.of(
            "status", 400,
            "error", "Parámetros inválidos",
            "detalle", errores
        ));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, Object>> validarParametro(
            HandlerMethodValidationException ex) {

        return ResponseEntity.badRequest().body(Map.of(
            "status", 400,
            "error", "Parámetro inválido",
            "detalle", "Revisa los parámetros de la solicitud"
        ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> cuerpoIncorrecto(
            HttpMessageNotReadableException ex) {

        return ResponseEntity.badRequest().body(Map.of(
            "status", 400,
            "error", "Solicitud incorrecta",
            "detalle", "El cuerpo JSON es inválido o tiene campos incompatibles"
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> argumentoIncorrecto(
            IllegalArgumentException ex) {

        return ResponseEntity.badRequest().body(Map.of(
            "status", 400,
            "error", "Parámetro incorrecto",
            "detalle", ex.getMessage()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> errorGeneral(
            Exception ex) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of(
                "status", 500,
                "error", "Error interno",
                "detalle", "No se pudo completar la operación"
            ));
    }
}