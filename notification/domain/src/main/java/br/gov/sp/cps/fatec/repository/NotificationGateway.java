package br.gov.sp.cps.fatec.repository;

import br.gov.sp.cps.fatec.entity.Notification;

public interface NotificationGateway {
    void broadcast(Notification notification);
    void sendToUser(String userId, Notification notification);
}
