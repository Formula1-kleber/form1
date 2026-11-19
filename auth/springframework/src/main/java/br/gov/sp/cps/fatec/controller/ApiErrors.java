package br.gov.sp.cps.fatec.controller;

import br.gov.sp.cps.fatec.exception.AuthException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
class ApiErrors {
    @ExceptionHandler(AuthException.class)
    ResponseEntity<Map<String, String>> handle(AuthException e) {
        HttpStatus status = switch (e.reason()) {
            case INVALID_DATA -> HttpStatus.BAD_REQUEST;
            case USERNAME_TAKEN -> HttpStatus.CONFLICT;
            case BAD_CREDENTIALS -> HttpStatus.UNAUTHORIZED;
        };
        return ResponseEntity.status(status).body(Map.of("code", e.reason().name(), "message", e.getMessage()));
    }
}
