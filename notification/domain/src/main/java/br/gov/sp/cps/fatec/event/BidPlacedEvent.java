package br.gov.sp.cps.fatec.event;

public record BidPlacedEvent(String lotId, String lotName, long amount, String bidderId, String bidderName,
                             String previousLeaderId, String previousLeaderName) {}
