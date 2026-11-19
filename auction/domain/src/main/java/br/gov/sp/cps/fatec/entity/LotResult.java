package br.gov.sp.cps.fatec.entity;

import java.time.Instant;

/** Resultado de um leilão encerrado, do ponto de vista de um participante. */
public record LotResult(String lotId, String lotName, Outcome outcome, String winnerName, long amount, Instant closedAt) {
    public enum Outcome { WON, LOST, UNSOLD }
}
