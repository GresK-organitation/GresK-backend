package com.gresk.modules.identity.infrastructure.security;

import com.gresk.infrastructure.port.AuthToken;
import com.gresk.modules.identity.application.port.out.JwtTokenGeneratorPort;
import com.gresk.modules.identity.domain.model.AccountId;
import com.gresk.shared.domain.Role;
import com.gresk.shared.domain.valueobject.Email;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class IdentityJwtTokenGenerator implements JwtTokenGeneratorPort {

    private final String secret;
    private final long expirationMs;

    public IdentityJwtTokenGenerator(
            @Value("${jwt.secret:esta_es_una_clave_secreta_muy_larga_de_al_menos_32_caracteres}") String secret,
            @Value("${jwt.expiration-ms:86400000}") long expirationMs
    ) {
        this.secret = secret;
        this.expirationMs = expirationMs;
    }

    @Override
    public AuthToken generate(AccountId accountId, Email email, Set<Role> roles) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        List<String> roleNames = roles.stream()
                .map(r -> "ROLE_" + r.name())
                .toList();

        String token = Jwts.builder()
                .subject(accountId.value().toString())
                .claim("email", email.value())
                .claim("roles", roleNames)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();

        return new AuthToken(token, expirationMs);
    }
}
