package br.gov.sp.cps.fatec.event;

/** Lance aceito (routing key: bid.placed). */
public record BidPlacedEvent(String lotId, String lotName, long amount, String bidderId, String bidderName,
                             String previousLeaderId, String previousLeaderName) {}
