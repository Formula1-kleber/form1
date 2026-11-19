package br.gov.sp.cps.fatec.configuration;

import br.gov.sp.cps.fatec.event.NotificationHub;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final NotificationHub hub;

    public WebSocketConfig(NotificationHub hub) {
        this.hub = hub;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(hub, "/ws")
                .addInterceptors(new HandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(ServerHttpRequest req, ServerHttpResponse res, WebSocketHandler h, Map<String, Object> attrs) {
                        String userId = req.getHeaders().getFirst("X-User-Id"); // injetado pelo gateway após validar o JWT
                        if (userId == null) {
                            res.setStatusCode(HttpStatus.UNAUTHORIZED);
                            return false;
                        }
                        attrs.put("userId", userId);
                        return true;
                    }

                    @Override
                    public void afterHandshake(ServerHttpRequest req, ServerHttpResponse res, WebSocketHandler h, Exception ex) {}
                })
                .setAllowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*");
    }
}
