package br.gov.sp.cps.fatec.event.dto;

import br.gov.sp.cps.fatec.event.BidPlacedEvent;

/** DTO da mensagem "bid.placed" recebida do RabbitMQ. */
public record BidPlacedMessage(String lotId, String lotName, long amount, String bidderId, String bidderName,
                               String previousLeaderId, String previousLeaderName) {

    public BidPlacedEvent toEvent() {
        return new BidPlacedEvent(lotId, lotName, amount, bidderId, bidderName, previousLeaderId, previousLeaderName);
    }
}
