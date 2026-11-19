package br.gov.sp.cps.fatec.event.dto;

import br.gov.sp.cps.fatec.event.AuctionClosedEvent;

import java.util.List;

/** DTO da mensagem "auction.closed". */
public record AuctionClosedMessage(String lotId, String lotName, boolean sold, String winnerId, String winnerName,
                                   long finalPrice, long referenceValue, long closedAt,
                                   List<String> participantIds) implements AuctionMessage {

    public static AuctionClosedMessage from(AuctionClosedEvent e) {
        return new AuctionClosedMessage(e.lotId(), e.lotName(), e.sold(), e.winnerId(), e.winnerName(),
                e.finalPrice(), e.referenceValue(), e.closedAt(), e.participantIds());
    }
}
