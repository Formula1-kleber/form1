package br.gov.sp.cps.fatec.repository.dto;

import br.gov.sp.cps.fatec.entity.Sale;

/** Contrato da resposta de GET /internal/auctions/closed (API de leilão). */
public record ClosedAuctionResponse(String lotId, String lotName, boolean sold, String buyerId, String buyerName,
                                    long finalPrice, long referenceValue, long closedAt) {

    public Sale toDomain() {
        return new Sale(lotId, lotName, buyerId, buyerName, finalPrice, referenceValue, sold, closedAt);
    }
}
