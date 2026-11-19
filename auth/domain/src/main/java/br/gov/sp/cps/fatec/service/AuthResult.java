package br.gov.sp.cps.fatec.service;

public record AuthResult(String token, String userId, String username, String role) {}
