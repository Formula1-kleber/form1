package br.gov.sp.cps.fatec.event;

import java.util.List;

public record AuctionClosedEvent(String lotId, String lotName, boolean sold, String winnerId, String winnerName,
                                 long finalPrice, long referenceValue, long closedAt, List<String> participantIds) {}
