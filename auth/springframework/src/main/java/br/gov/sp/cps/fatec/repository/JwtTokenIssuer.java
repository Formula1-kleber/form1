package br.gov.sp.cps.fatec.repository;

import br.gov.sp.cps.fatec.entity.User;
import br.gov.sp.cps.fatec.repository.TokenIssuer;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
class JwtTokenIssuer implements TokenIssuer {
    private final SecretKey key;
    private final long ttlMillis;

    JwtTokenIssuer(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.ttl-minutes}") long ttlMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.ttlMillis = ttlMinutes * 60_000;
    }

    @Override
    public String issue(User user) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(user.id())
                .claim("username", user.username())
                .claim("role", user.role())
                .issuedAt(new Date(now))
                .expiration(new Date(now + ttlMillis))
                .signWith(key)
                .compact();
    }
}
