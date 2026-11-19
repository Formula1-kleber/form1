package br.gov.sp.cps.fatec.event;

import java.util.List;

/** Tempo do lote acabou (routing key: auction.closed). participantIds = todos que deram algum lance. */
public record AuctionClosedEvent(String lotId, String lotName, boolean sold, String winnerId, String winnerName,
                                 long finalPrice, long referenceValue, long closedAt, List<String> participantIds) {}
