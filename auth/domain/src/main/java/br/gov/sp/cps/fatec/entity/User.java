package br.gov.sp.cps.fatec.entity;

public record User(String id, String username, String passwordHash, String role) {}
