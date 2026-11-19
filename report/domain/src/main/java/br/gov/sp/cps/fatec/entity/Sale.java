package br.gov.sp.cps.fatec.entity;

/** Leilão encerrado (vendido ou não), como aparece no relatório. O id é o do lote. */
public record Sale(String lotId, String lotName, String buyerId, String buyerName,
                   long finalPrice, long referenceValue, boolean sold, long closedAt) {}
