package br.gov.sp.cps.fatec.repository;

import br.gov.sp.cps.fatec.event.AuctionClosedEvent;
import br.gov.sp.cps.fatec.event.BidPlacedEvent;
import br.gov.sp.cps.fatec.event.LotCreatedEvent;

public interface AuctionEventPublisher {
    void bidPlaced(BidPlacedEvent event);
    /** @return true se a mensagem foi publicada com sucesso. */
    boolean auctionClosed(AuctionClosedEvent event);
    void lotCreated(LotCreatedEvent event);
}
