package br.gov.sp.cps.fatec.event.dto;

import br.gov.sp.cps.fatec.event.LotCreatedEvent;

/** DTO da mensagem "lot.created" recebida do RabbitMQ. */
public record LotCreatedMessage(String lotId, String lotName) {

    public LotCreatedEvent toEvent() {
        return new LotCreatedEvent(lotId, lotName);
    }
}
