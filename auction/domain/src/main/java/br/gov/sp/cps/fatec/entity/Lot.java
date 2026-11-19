package br.gov.sp.cps.fatec.entity;

import java.time.Instant;
import java.util.List;

public record Lot(
        String id,
        String name,
        String description,
        long referenceValue,
        long startPrice,
        long minIncrement,
        long currentBid,
        String leaderId,
        String leaderName,
        Instant endsAt,
        Status status,
        long finalPrice,
        List<Bid> bids,
        boolean closedEventSent) { // false até a mensagem auction.closed ser publicada com sucesso

    public enum Status { OPEN, SOLD, UNSOLD }

    public record Bid(String userId, String userName, long amount, Instant at) {}

    public boolean isOpenAt(Instant moment) {
        return status == Status.OPEN && endsAt.isAfter(moment);
    }

    public long minimumNextBid() {
        return Math.max(startPrice, currentBid + minIncrement);
    }
}
