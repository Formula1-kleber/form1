package br.gov.sp.cps.fatec.service;

import br.gov.sp.cps.fatec.event.BidPlacedEvent;
import br.gov.sp.cps.fatec.exception.BidRejectedException;
import br.gov.sp.cps.fatec.exception.BidRejectedException.Reason;
import br.gov.sp.cps.fatec.entity.Lot;
import br.gov.sp.cps.fatec.repository.AuctionEventPublisher;
import br.gov.sp.cps.fatec.repository.LotRepository;

import java.text.NumberFormat;
import java.time.Instant;
import java.util.Locale;

public class PlaceBidUseCase {
    private final LotRepository lots;
    private final AuctionEventPublisher events;

    public PlaceBidUseCase(LotRepository lots, AuctionEventPublisher events) {
        this.lots = lots;
        this.events = events;
    }

    public Lot execute(String lotId, String userId, String userName, long amount) {
        Lot lot = lots.findById(lotId)
                .orElseThrow(() -> new BidRejectedException(Reason.NOT_FOUND, "Lote não encontrado."));

        Lot before = lots.tryPlaceBid(lot, userId, userName, amount, Instant.now())
                .orElseThrow(() -> explainRejection(lotId, userId, amount));

        events.bidPlaced(new BidPlacedEvent(lotId, lot.name(), amount, userId, userName,
                before.leaderId(), before.leaderName()));
        return lots.findById(lotId).orElseThrow();
    }

    private BidRejectedException explainRejection(String lotId, String userId, long amount) {
        Lot l = lots.findById(lotId).orElse(null);
        if (l == null) return new BidRejectedException(Reason.NOT_FOUND, "Lote não encontrado.");
        if (!l.isOpenAt(Instant.now())) return new BidRejectedException(Reason.CLOSED, "Este leilão já foi encerrado.");
        if (userId.equals(l.leaderId()))
            return new BidRejectedException(Reason.ALREADY_LEADING, "Você já está com o maior lance neste lote.");
        if (amount == l.currentBid())
            return new BidRejectedException(Reason.SAME_BID,
                    "Outro participante acabou de dar o mesmo lance de " + brl(amount) + ". Escolha um valor diferente.");
        return new BidRejectedException(Reason.TOO_LOW, "O lance mínimo agora é " + brl(l.minimumNextBid()) + ".");
    }

    private static String brl(long v) {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(v).replace('\u00a0', ' ');
    }
}
