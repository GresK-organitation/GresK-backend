package com.gresk.modules.email.infrastructure.gmail;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.GmailScopes;
import com.gresk.modules.email.domain.model.PromoterGmailToken;
import com.gresk.modules.email.domain.port.out.PromoterGmailTokenRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Flujo OAuth de Gmail:
 *  1. connect: genera la URL de autorización con un state anti-CSRF de un solo uso
 *  2. callback: intercambia el code por tokens, lee el perfil, registra el
 *     watch Pub/Sub y persiste el token (cifrado en el adaptador JPA)
 *  3. disconnect: detiene el watch y borra las credenciales
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GmailOAuthService {

    private static final List<String> SCOPES = List.of(GmailScopes.GMAIL_READONLY);
    private static final long STATE_TTL_SECONDS = 600;

    private final GmailProperties                  properties;
    private final GmailApiAdapter                  gmailApiAdapter;
    private final PromoterGmailTokenRepositoryPort tokenRepository;

    private final SecureRandom random = new SecureRandom();
    private final Map<String, PendingState> pendingStates = new ConcurrentHashMap<>();

    /** Paso 1: URL de autorización de Google para esta promotora. */
    public String buildAuthorizationUrl(PromoterId promoterId) {
        byte[] nonce = new byte[24];
        random.nextBytes(nonce);
        String state = Base64.getUrlEncoder().withoutPadding().encodeToString(nonce);

        purgeExpiredStates();
        pendingStates.put(state, new PendingState(
                promoterId, Instant.now().plusSeconds(STATE_TTL_SECONDS)));

        return new GoogleAuthorizationCodeRequestUrl(
                properties.clientId(), properties.redirectUri(), SCOPES)
                .setState(state)
                .setAccessType("offline")
                .set("prompt", "consent")
                .build();
    }

    /** Paso 2: callback de Google con code + state. */
    @Transactional
    public PromoterGmailToken handleCallback(String code, String state) {
        PendingState pending = pendingStates.remove(state);
        if (pending == null || pending.isExpired()) {
            throw new IllegalArgumentException("Invalid or expired OAuth state");
        }

        try {
            GoogleTokenResponse tokens = new GoogleAuthorizationCodeTokenRequest(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    properties.clientId(), properties.clientSecret(),
                    code, properties.redirectUri())
                    .execute();

            Instant expiry = tokens.getExpiresInSeconds() != null
                    ? Instant.now().plusSeconds(tokens.getExpiresInSeconds())
                    : null;

            PromoterGmailToken token = PromoterGmailToken.connect(
                    pending.promoterId(),
                    tokens.getAccessToken(),
                    tokens.getRefreshToken(),
                    expiry,
                    null);

            var gmail = gmailApiAdapter.clientFor(token);
            String gmailAddress = gmailApiAdapter.profileEmailAddress(gmail);

            token = PromoterGmailToken.connect(
                    pending.promoterId(),
                    tokens.getAccessToken(),
                    tokens.getRefreshToken(),
                    expiry,
                    gmailAddress);

            registerWatchIfConfigured(token);

            tokenRepository.save(token);
            log.info("Gmail connected for promoter {} ({})", pending.promoterId(), gmailAddress);
            return token;

        } catch (Exception e) {
            throw new IllegalStateException("Gmail OAuth callback failed", e);
        }
    }

    /** Paso 3: desconexión — detiene el watch y elimina las credenciales. */
    @Transactional
    public void disconnect(PromoterId promoterId) {
        tokenRepository.findByPromoterId(promoterId).ifPresent(token -> {
            gmailApiAdapter.stopWatch(gmailApiAdapter.clientFor(token));
            tokenRepository.deleteByPromoterId(promoterId);
            log.info("Gmail disconnected for promoter {}", promoterId);
        });
    }

    private void registerWatchIfConfigured(PromoterGmailToken token) {
        if (properties.pubsubTopic().isBlank()) {
            log.warn("gresk.email.gmail.pubsub-topic not set — skipping Gmail watch registration; "
                    + "real-time notifications will be unavailable");
            return;
        }
        try {
            GmailApiAdapter.WatchResult watch =
                    gmailApiAdapter.watchMailbox(gmailApiAdapter.clientFor(token));
            token.registerWatch(watch.expiration(), watch.historyId());
        } catch (Exception e) {
            log.error("Gmail watch registration failed (token saved anyway): {}", e.getMessage());
        }
    }

    private void purgeExpiredStates() {
        pendingStates.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    private record PendingState(PromoterId promoterId, Instant expiresAt) {
        boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }
}
