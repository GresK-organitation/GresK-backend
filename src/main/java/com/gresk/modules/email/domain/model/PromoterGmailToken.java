package com.gresk.modules.email.domain.model;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.time.Instant;

/**
 * Credenciales OAuth de Gmail de una promotora (relación 1:1 con users).
 * Los tokens viajan en claro por el dominio; el cifrado AES-256-GCM se
 * aplica en el adaptador de persistencia.
 */
public final class PromoterGmailToken {

    private final PromoterId promoterId;
    private final Instant    connectedAt;

    private String  accessToken;
    private String  refreshToken;
    private Instant tokenExpiry;
    private String  gmailAddress;
    private Instant watchExpiry;
    private Long    lastHistoryId;

    private PromoterGmailToken(PromoterId promoterId, String accessToken, String refreshToken,
                               Instant tokenExpiry, String gmailAddress, Instant watchExpiry,
                               Long lastHistoryId, Instant connectedAt) {
        this.promoterId    = promoterId;
        this.accessToken   = accessToken;
        this.refreshToken  = refreshToken;
        this.tokenExpiry   = tokenExpiry;
        this.gmailAddress  = gmailAddress;
        this.watchExpiry   = watchExpiry;
        this.lastHistoryId = lastHistoryId;
        this.connectedAt   = connectedAt;
    }

    // ── Factories ─────────────────────────────────────────────────────────────

    public static PromoterGmailToken connect(PromoterId promoterId, String accessToken,
                                             String refreshToken, Instant tokenExpiry,
                                             String gmailAddress) {
        return new PromoterGmailToken(promoterId, accessToken, refreshToken,
                tokenExpiry, gmailAddress, null, null, Instant.now());
    }

    public static PromoterGmailToken reconstitute(
            PromoterId promoterId, String accessToken, String refreshToken,
            Instant tokenExpiry, String gmailAddress, Instant watchExpiry,
            Long lastHistoryId, Instant connectedAt) {
        return new PromoterGmailToken(promoterId, accessToken, refreshToken,
                tokenExpiry, gmailAddress, watchExpiry, lastHistoryId, connectedAt);
    }

    // ── Mutadores de dominio ──────────────────────────────────────────────────

    /** Tokens renovados tras un refresh OAuth. */
    public void updateTokens(String accessToken, Instant tokenExpiry) {
        this.accessToken = accessToken;
        this.tokenExpiry = tokenExpiry;
    }

    /** Registro (o renovación) del watch de Gmail Pub/Sub. */
    public void registerWatch(Instant watchExpiry, Long historyId) {
        this.watchExpiry = watchExpiry;
        if (historyId != null) {
            this.lastHistoryId = historyId;
        }
    }

    /** Avanza el cursor de sincronización incremental. */
    public void trackHistory(Long historyId) {
        if (historyId != null && (lastHistoryId == null || historyId > lastHistoryId)) {
            this.lastHistoryId = historyId;
        }
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public PromoterId getPromoterId()  { return promoterId; }
    public String getAccessToken()     { return accessToken; }
    public String getRefreshToken()    { return refreshToken; }
    public Instant getTokenExpiry()    { return tokenExpiry; }
    public String getGmailAddress()    { return gmailAddress; }
    public Instant getWatchExpiry()    { return watchExpiry; }
    public Long getLastHistoryId()     { return lastHistoryId; }
    public Instant getConnectedAt()    { return connectedAt; }
}
