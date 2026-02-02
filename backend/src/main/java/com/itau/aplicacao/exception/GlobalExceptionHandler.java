package com.itau.aplicacao.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Void> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
        String motivo = cause.getMessage() != null ? cause.getMessage() : ex.getMessage();
        log.warn("Requisição com corpo inválido (BAD_REQUEST): motivo={}", motivo);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        var bindingResult = ex.getBindingResult();
        for (FieldError err : bindingResult.getFieldErrors()) {
            String campo = err.getField();
            String motivo = err.getDefaultMessage() != null ? err.getDefaultMessage() : "inválido";
            Object valorRejeitado = err.getRejectedValue();
            log.warn("Validação falhou (422): campo='{}' motivo='{}' valorRejeitado={}",
                    campo, motivo, valorRejeitado);
        }
        for (var err : bindingResult.getGlobalErrors()) {
            String motivo = err.getDefaultMessage() != null ? err.getDefaultMessage() : "erro de validação";
            log.warn("Validação falhou (422): {}", motivo);
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).build();
    }
}
