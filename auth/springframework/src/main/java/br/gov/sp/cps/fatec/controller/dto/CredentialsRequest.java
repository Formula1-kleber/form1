package br.gov.sp.cps.fatec.controller.dto;

/** Corpo de /login e /register. */
public record CredentialsRequest(String username, String password) {}
