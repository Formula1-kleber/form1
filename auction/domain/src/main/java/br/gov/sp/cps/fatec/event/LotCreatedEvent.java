package br.gov.sp.cps.fatec.event;

/** Novo lote aberto pelo admin (routing key: lot.created). */
public record LotCreatedEvent(String lotId, String lotName) {}
