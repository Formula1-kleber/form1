package br.gov.sp.cps.fatec.repository;

import br.gov.sp.cps.fatec.entity.Lot;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

/** Representação do lote no MongoDB. O domínio não conhece esta classe. */
@Document("lots")
public record LotDocument(
        @Id String id,
        String name,
        String description,
        long referenceValue,
        long startPrice,
        long minIncrement,
        long currentBid,
        String leaderId,
        String leaderName,
        Instant endsAt,
        Lot.Status status,
        long finalPrice,
        List<BidDocument> bids,
        Boolean closedEventSent) {

    public record BidDocument(String userId, String userName, long amount, Instant at) {}

    static LotDocument from(Lot l) {
        return new LotDocument(l.id(), l.name(), l.description(), l.referenceValue(), l.startPrice(), l.minIncrement(),
                l.currentBid(), l.leaderId(), l.leaderName(), l.endsAt(), l.status(), l.finalPrice(),
                l.bids().stream().map(b -> new BidDocument(b.userId(), b.userName(), b.amount(), b.at())).toList(),
                l.closedEventSent());
    }

    Lot toDomain() {
        List<Lot.Bid> domainBids = bids == null ? List.of()
                : bids.stream().map(b -> new Lot.Bid(b.userId(), b.userName(), b.amount(), b.at())).toList();
        return new Lot(id, name, description, referenceValue, startPrice, minIncrement, currentBid, leaderId,
                leaderName, endsAt, status, finalPrice, domainBids, Boolean.TRUE.equals(closedEventSent));
    }
}
