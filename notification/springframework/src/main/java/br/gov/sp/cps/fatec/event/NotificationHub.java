package br.gov.sp.cps.fatec.event;

import br.gov.sp.cps.fatec.entity.Notification;
import br.gov.sp.cps.fatec.repository.NotificationGateway;
import tools.jackson.databind.json.JsonMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** Adaptador de saída: entrega as notificações do domínio pelas conexões WebSocket abertas. */
@Component
public class NotificationHub extends TextWebSocketHandler implements NotificationGateway {

    private final Map<String, Set<WebSocketSession>> sessions = new ConcurrentHashMap<>();
    private final JsonMapper mapper = JsonMapper.builder().build();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");
        sessions.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet())
                .add(new ConcurrentWebSocketSessionDecorator(session, 5000, 65536));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String userId = (String) session.getAttributes().get("userId");
        Set<WebSocketSession> set = sessions.get(userId);
        if (set != null) set.removeIf(s -> s.getId().equals(session.getId()));
    }

    @Override
    public void broadcast(Notification notification) {
        Map<String, Object> json = toJson(notification);
        sessions.values().forEach(set -> set.forEach(s -> send(s, json)));
    }

    @Override
    public void sendToUser(String userId, Notification notification) {
        Set<WebSocketSession> set = sessions.get(userId);
        if (set == null) return;
        Map<String, Object> json = toJson(notification);
        set.forEach(s -> send(s, json));
    }

    /** Formato esperado pelo front-end. */
    private Map<String, Object> toJson(Notification n) {
        if (n instanceof Notification.LotUpdated u)
            return Map.of("type", "LOT_UPDATED", "lotId", u.lotId(), "currentBid", u.currentBid(),
                    "leaderId", u.leaderId(), "leaderName", u.leaderName());
        if (n instanceof Notification.LotClosed c)
            return Map.of("type", "LOT_CLOSED", "lotId", c.lotId(), "sold", c.sold());
        if (n instanceof Notification.LotCreated created)
            return Map.of("type", "LOT_CREATED", "lotId", created.lotId());
        if (n instanceof Notification.Outbid o)
            return Map.of("type", "OUTBID", "lotId", o.lotId(), "lotName", o.lotName(),
                    "amount", o.amount(), "bidderName", o.bidderName());
        Notification.AuctionResult r = (Notification.AuctionResult) n;
        return Map.of("type", "AUCTION_RESULT", "lotName", r.lotName(), "outcome", r.outcome().name(),
                "winnerName", r.winnerName(), "amount", r.amount());
    }

    private void send(WebSocketSession session, Map<String, Object> json) {
        try {
            if (session.isOpen()) session.sendMessage(new TextMessage(mapper.writeValueAsString(json)));
        } catch (Exception ignored) {
            // conexão quebrada: será removida no afterConnectionClosed
        }
    }
}
