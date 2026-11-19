package br.gov.sp.cps.fatec.service;

import br.gov.sp.cps.fatec.event.LotCreatedEvent;
import br.gov.sp.cps.fatec.entity.Notification;
import br.gov.sp.cps.fatec.repository.NotificationGateway;

public class NotifyLotCreatedUseCase {
    private final NotificationGateway gateway;

    public NotifyLotCreatedUseCase(NotificationGateway gateway) {
        this.gateway = gateway;
    }

    public void execute(LotCreatedEvent e) {
        gateway.broadcast(new Notification.LotCreated(e.lotId()));
    }
}
