package br.gov.sp.cps.fatec.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@RestControllerAdvice
class ApiErrors {

    /** A API de leilão está fora do ar: o front recebe uma mensagem clara em vez de um erro genérico. */
    @ExceptionHandler(RestClientException.class)
    ResponseEntity<Map<String, String>> auctionUnavailable(RestClientException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("code", "LEILAO_INDISPONIVEL", "message", "Não foi possível consultar a API de leilão."));
    }
}
