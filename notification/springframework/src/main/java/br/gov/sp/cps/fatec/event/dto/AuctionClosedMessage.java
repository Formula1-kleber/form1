package br.gov.sp.cps.fatec.event.dto;

import br.gov.sp.cps.fatec.event.AuctionClosedEvent;

import java.util.List;

/** DTO da mensagem "auction.closed" recebida do RabbitMQ. */
public record AuctionClosedMessage(String lotId, String lotName, boolean sold, String winnerId, String winnerName,
                                   long finalPrice, long referenceValue, long closedAt, List<String> participantIds) {

    public AuctionClosedEvent toEvent() {
        return new AuctionClosedEvent(lotId, lotName, sold, winnerId, winnerName, finalPrice, referenceValue,
                closedAt, participantIds);
    }
}
