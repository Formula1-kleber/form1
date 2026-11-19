package br.gov.sp.cps.fatec.configuration;

import br.gov.sp.cps.fatec.entity.Decision;
import br.gov.sp.cps.fatec.service.AuthorizeRequestUseCase;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * Único ponto de autenticação do sistema: lê o JWT do cookie HttpOnly, valida a assinatura e repassa
 * a identidade para os serviços internos via headers X-User-*. A regra de quem pode acessar o quê
 * fica no domínio ({@link AuthorizeRequestUseCase}); aqui só se traduz o resultado para HTTP.
 */
@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private final SecretKey key;
    private final String cookieName;
    private final AuthorizeRequestUseCase authorize;

    public JwtAuthFilter(@Value("${app.jwt.secret}") String secret,
                         @Value("${app.cookie.name}") String cookieName,
                         AuthorizeRequestUseCase authorize) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.cookieName = cookieName;
        this.authorize = authorize;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest req = exchange.getRequest();
        String path = req.getURI().getPath();

        Claims claims = readClaims(req);
        String role = claims == null ? null : claims.get("role", String.class);

        Decision decision = authorize.execute(req.getMethod().name(), path, role);
        if (decision == Decision.UNAUTHENTICATED) return reject(exchange, HttpStatus.UNAUTHORIZED);
        if (decision == Decision.FORBIDDEN) return reject(exchange, HttpStatus.FORBIDDEN);

        // Nunca confia em X-User-* vindo do cliente: remove e, havendo sessão válida, reescreve.
        ServerHttpRequest mutated = req.mutate().headers(h -> {
            h.remove("X-User-Id");
            h.remove("X-User-Name");
            h.remove("X-User-Role");
            if (claims != null) {
                h.set("X-User-Id", claims.getSubject());
                h.set("X-User-Name", claims.get("username", String.class));
                h.set("X-User-Role", claims.get("role", String.class));
            }
        }).build();
        return chain.filter(exchange.mutate().request(mutated).build());
    }

    /** @return as claims do token válido ou {@code null} (sem token, expirado ou adulterado). */
    private Claims readClaims(ServerHttpRequest req) {
        String token = extractToken(req);
        if (token == null) return null;
        try {
            return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        } catch (Exception e) {
            return null;
        }
    }

    /** O token vem do cookie HttpOnly (o front não tem acesso a ele via JavaScript). */
    private String extractToken(ServerHttpRequest req) {
        HttpCookie cookie = req.getCookies().getFirst(cookieName);
        if (cookie != null && !cookie.getValue().isBlank()) return cookie.getValue();
        String auth = req.getHeaders().getFirst("Authorization"); // útil para testar com Postman/curl
        if (auth != null && auth.startsWith("Bearer ")) return auth.substring(7);
        return null;
    }

    private Mono<Void> reject(ServerWebExchange exchange, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
