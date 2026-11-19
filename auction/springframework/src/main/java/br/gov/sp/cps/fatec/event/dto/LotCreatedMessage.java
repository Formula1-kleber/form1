package br.gov.sp.cps.fatec.event.dto;

import br.gov.sp.cps.fatec.event.LotCreatedEvent;

/** DTO da mensagem "lot.created". */
public record LotCreatedMessage(String lotId, String lotName) implements AuctionMessage {

    public static LotCreatedMessage from(LotCreatedEvent e) {
        return new LotCreatedMessage(e.lotId(), e.lotName());
    }
}
