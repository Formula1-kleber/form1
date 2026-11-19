package br.gov.sp.cps.fatec.service;

import br.gov.sp.cps.fatec.event.AuctionClosedEvent;
import br.gov.sp.cps.fatec.entity.Lot;
import br.gov.sp.cps.fatec.repository.AuctionEventPublisher;
import br.gov.sp.cps.fatec.repository.LotRepository;

import java.time.Instant;
import java.util.List;

/**
 * Fluxo assíncrono: encerra lotes vencidos e avisa os demais serviços por mensagem.
 */
public class CloseExpiredLotsUseCase {
    private static final long RETRY_EVERY_MS = 5000;

    private final LotRepository lots;
    private final AuctionEventPublisher events;
    private final double reserveRatio;
    private long lastRetry = 0;

    public CloseExpiredLotsUseCase(LotRepository lots, AuctionEventPublisher events, double reserveRatio) {
        this.lots = lots;
        this.events = events;
        this.reserveRatio = reserveRatio;
    }

    public void execute() {
        Instant now = Instant.now();
        for (Lot lot : lots.findExpired(now)) {
            boolean sold = lot.leaderId() != null && lot.currentBid() >= lot.referenceValue() * reserveRatio;
            // tryClose só funciona se o lote ainda estiver OPEN: garante um único encerramento.
            lots.tryClose(lot.id(), sold ? Lot.Status.SOLD : Lot.Status.UNSOLD, sold ? lot.currentBid() : 0)
                    .ifPresent(closed -> publishClosed(closed, now.toEpochMilli()));
        }
        if (System.currentTimeMillis() - lastRetry >= RETRY_EVERY_MS) {
            lastRetry = System.currentTimeMillis();
            for (Lot pending : lots.findClosedWithPendingEvent()) {
                publishClosed(pending, pending.endsAt().toEpochMilli());
            }
        }
    }

    private void publishClosed(Lot closed, long closedAt) {
        boolean sold = closed.status() == Lot.Status.SOLD;
        List<String> participants = closed.bids() == null ? List.of()
                : closed.bids().stream().map(Lot.Bid::userId).distinct().toList();
        boolean sent = events.auctionClosed(new AuctionClosedEvent(closed.id(), closed.name(), sold, closed.leaderId(),
                closed.leaderName(), closed.finalPrice(), closed.referenceValue(), closedAt, participants));
        if (sent) lots.markClosedEventSent(closed.id());
    }
}
