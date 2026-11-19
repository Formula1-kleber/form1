package br.gov.sp.cps.fatec.service;

import br.gov.sp.cps.fatec.event.AuctionClosedEvent;
import br.gov.sp.cps.fatec.entity.Notification;
import br.gov.sp.cps.fatec.entity.Notification.Outcome;
import br.gov.sp.cps.fatec.repository.NotificationGateway;

import java.util.List;

public class NotifyAuctionClosedUseCase {
    private final NotificationGateway gateway;

    public NotifyAuctionClosedUseCase(NotificationGateway gateway) {
        this.gateway = gateway;
    }

    public void execute(AuctionClosedEvent e) {
        gateway.broadcast(new Notification.LotClosed(e.lotId(), e.sold()));

        String winner = e.winnerName() == null ? "" : e.winnerName();
        List<String> participants = e.participantIds() == null ? List.of() : e.participantIds();
        for (String userId : participants) {
            Outcome outcome = !e.sold() ? Outcome.UNSOLD : userId.equals(e.winnerId()) ? Outcome.WON : Outcome.LOST;
            gateway.sendToUser(userId, new Notification.AuctionResult(e.lotName(), outcome, winner, e.finalPrice()));
        }
    }
}
