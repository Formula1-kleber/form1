package br.gov.sp.cps.fatec.event.dto;

import br.gov.sp.cps.fatec.event.BidPlacedEvent;

/** DTO da mensagem "bid.placed". */
public record BidPlacedMessage(String lotId, String lotName, long amount, String bidderId, String bidderName,
                               String previousLeaderId, String previousLeaderName) implements AuctionMessage {

    public static BidPlacedMessage from(BidPlacedEvent e) {
        return new BidPlacedMessage(e.lotId(), e.lotName(), e.amount(), e.bidderId(), e.bidderName(),
                e.previousLeaderId(), e.previousLeaderName());
    }
}
