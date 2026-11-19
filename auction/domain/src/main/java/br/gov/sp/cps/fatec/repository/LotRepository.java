package br.gov.sp.cps.fatec.repository;

import br.gov.sp.cps.fatec.entity.Lot;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface LotRepository {
    long count();
    Lot insert(Lot lot);
    Optional<Lot> findById(String id);
    List<Lot> findOpen(Instant now);
    List<Lot> findExpired(Instant now);
    List<Lot> findClosed();
    List<Lot> findClosedWithBidFrom(String userId, int limit);

    /** Lotes já encerrados cuja mensagem auction.closed ainda não foi publicada com sucesso. */
    List<Lot> findClosedWithPendingEvent();

    void markClosedEventSent(String lotId);

    Optional<Lot> tryPlaceBid(Lot lot, String userId, String userName, long amount, Instant now);

    Optional<Lot> tryClose(String lotId, Lot.Status newStatus, long finalPrice);
}
