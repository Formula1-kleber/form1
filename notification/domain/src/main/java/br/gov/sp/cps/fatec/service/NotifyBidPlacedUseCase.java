package br.gov.sp.cps.fatec.service;

import br.gov.sp.cps.fatec.event.BidPlacedEvent;
import br.gov.sp.cps.fatec.entity.Notification;
import br.gov.sp.cps.fatec.repository.NotificationGateway;

public class NotifyBidPlacedUseCase {
    private final NotificationGateway gateway;

    public NotifyBidPlacedUseCase(NotificationGateway gateway) {
        this.gateway = gateway;
    }

    public void execute(BidPlacedEvent e) {

        gateway.broadcast(new Notification.LotUpdated(e.lotId(), e.amount(), e.bidderId(), e.bidderName()));

        if (e.previousLeaderId() != null && !e.previousLeaderId().equals(e.bidderId())) {
            gateway.sendToUser(e.previousLeaderId(), new Notification.Outbid(e.lotId(), e.lotName(), e.amount(), e.bidderName()));
        }
    }
}
