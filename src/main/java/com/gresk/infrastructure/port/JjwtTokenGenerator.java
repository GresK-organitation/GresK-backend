package com.gresk.infrastructure.port;

import com.gresk.modules.promoter.domain.valueobject.Email;
import com.gresk.modules.promoter.domain.valueobject.PromoterId;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JjwtTokenGenerator implements JwtTokenGenerator {

    private final String secret;
    private final long expirationMs;

    public JjwtTokenGenerator(
            @Value("${jwt.secret:esta_es_una_clave_secreta_muy_larga_de_al_menos_32_caracteres}") String secret,
            @Value("${jwt.expiration-ms:86400000}") long expirationMs
    ) {
        this.secret = secret;
        this.expirationMs = expirationMs;
    }

    @Override
    public AuthToken generate(PromoterId subject, Email email) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        String token = Jwts.builder()
                .subject(subject.value().toString())
                .claim("email", email.value())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();

        return new AuthToken(token, expirationMs);
    }
}
