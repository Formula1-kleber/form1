package br.gov.sp.cps.fatec.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Gera o cookie de sessão. HttpOnly: o JavaScript do site não consegue ler o token (protege contra XSS).
 * SameSite=Lax: o navegador não envia o cookie em requisições vindas de outros sites (protege contra CSRF).
 */
@Component
public class AuthCookieFactory {

    private final String name;
    private final boolean secure;
    private final String sameSite;
    private final Duration ttl;

    AuthCookieFactory(@Value("${app.cookie.name}") String name,
                      @Value("${app.cookie.secure}") boolean secure,
                      @Value("${app.cookie.same-site}") String sameSite,
                      @Value("${app.jwt.ttl-minutes}") long ttlMinutes) {
        this.name = name;
        this.secure = secure;
        this.sameSite = sameSite;
        this.ttl = Duration.ofMinutes(ttlMinutes);
    }

    /** Cookie que guarda o JWT enquanto a sessão for válida. */
    public ResponseCookie issue(String token) {
        return base(token).maxAge(ttl).build();
    }

    /** Cookie vazio e já expirado: instrui o navegador a apagar a sessão (logout). */
    public ResponseCookie clear() {
        return base("").maxAge(Duration.ZERO).build();
    }

    private ResponseCookie.ResponseCookieBuilder base(String value) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/");
    }
}
