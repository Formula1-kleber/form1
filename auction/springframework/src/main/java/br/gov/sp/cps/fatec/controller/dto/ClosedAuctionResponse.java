package br.gov.sp.cps.fatec.controller.dto;

import br.gov.sp.cps.fatec.entity.Lot;

/** Leilão encerrado, no formato consumido pelo relatório. */
public record ClosedAuctionResponse(String lotId, String lotName, boolean sold, String buyerId, String buyerName,
                                    long finalPrice, long referenceValue, long closedAt) {

    public static ClosedAuctionResponse from(Lot l) {
        boolean sold = l.status() == Lot.Status.SOLD;
        return new ClosedAuctionResponse(l.id(), l.name(), sold,
                sold ? l.leaderId() : null, sold ? l.leaderName() : null,
                l.finalPrice(), l.referenceValue(), l.endsAt().toEpochMilli());
    }
}
