package br.gov.sp.cps.fatec.controller;

import br.gov.sp.cps.fatec.exception.BidRejectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
class ApiErrors {

    private static final Logger log = LoggerFactory.getLogger(ApiErrors.class);

    @ExceptionHandler(BidRejectedException.class)
    ResponseEntity<Map<String, String>> handle(BidRejectedException e) {
        HttpStatus status = switch (e.reason()) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case INVALID_DATA -> HttpStatus.BAD_REQUEST;
            case NOT_ALLOWED -> HttpStatus.FORBIDDEN;
            default -> HttpStatus.CONFLICT;
        };
        return ResponseEntity.status(status).body(Map.of("code", e.reason().code(), "message", e.getMessage()));
    }

    /** Qualquer erro não previsto vira JSON com a causa, para o front mostrar algo útil (e o log guarda o stack trace). */
    @ExceptionHandler(Exception.class)
    ResponseEntity<Map<String, String>> unexpected(Exception e) {
        if (e instanceof ErrorResponse er) {
            return ResponseEntity.status(er.getStatusCode())
                    .body(Map.of("code", "ERRO_REQUISICAO", "message", String.valueOf(er.getBody().getDetail())));
        }
        log.error("Erro inesperado no auction-service", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("code", "ERRO_INTERNO", "message", "Erro interno (" + e.getClass().getSimpleName() + "): " + e.getMessage()));
    }
}
