package br.gov.sp.cps.fatec.service;

import br.gov.sp.cps.fatec.event.LotCreatedEvent;
import br.gov.sp.cps.fatec.exception.BidRejectedException;
import br.gov.sp.cps.fatec.exception.BidRejectedException.Reason;
import br.gov.sp.cps.fatec.entity.Lot;
import br.gov.sp.cps.fatec.repository.AuctionEventPublisher;
import br.gov.sp.cps.fatec.repository.LotRepository;

import java.time.Instant;
import java.util.List;

public class CreateLotUseCase {

    public record Command(String name, String description, long referenceValue, long startPrice, long durationSeconds) {}

    static final long MIN_INCREMENT = 500;

    private final LotRepository lots;
    private final AuctionEventPublisher events;

    public CreateLotUseCase(LotRepository lots, AuctionEventPublisher events) {
        this.lots = lots;
        this.events = events;
    }

    public Lot execute(Command c) {
        if (c.name() == null || c.name().isBlank() || c.referenceValue() <= 0 || c.startPrice() <= 0 || c.durationSeconds() <= 0)
            throw new BidRejectedException(Reason.INVALID_DATA, "Informe nome, valores e duração maiores que zero.");
        Lot created = lots.insert(new Lot(null, c.name().trim(), c.description(), c.referenceValue(), c.startPrice(), MIN_INCREMENT,
                0, null, null, Instant.now().plusSeconds(c.durationSeconds()), Lot.Status.OPEN, 0, List.of(), false));
        events.lotCreated(new LotCreatedEvent(created.id(), created.name())); // avisa quem está online: o lote aparece na hora
        return created;
    }
}
