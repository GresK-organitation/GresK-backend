package com.gresk.modules.email.domain.model;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.time.Instant;

/**
 * Credenciales OAuth de Gmail de una promotora (relación 1:1 con users).
 * Los tokens se persisten en claro en esta fase; el cifrado AES-256-GCM
 * se introduce en el Issue #2 del EIE.
 */
public final class PromoterGmailToken {

    private final PromoterId promoterId;
    private final Instant    connectedAt;

    private String  accessToken;
    private String  refreshToken;
    private Instant tokenExpiry;
    private String  gmailAddress;
    private Instant watchExpiry;

    private PromoterGmailToken(PromoterId promoterId, String accessToken, String refreshToken,
                               Instant tokenExpiry, String gmailAddress, Instant watchExpiry,
                               Instant connectedAt) {
        this.promoterId   = promoterId;
        this.accessToken  = accessToken;
        this.refreshToken = refreshToken;
        this.tokenExpiry  = tokenExpiry;
        this.gmailAddress = gmailAddress;
        this.watchExpiry  = watchExpiry;
        this.connectedAt  = connectedAt;
    }

    // ── Factories ─────────────────────────────────────────────────────────────

    public static PromoterGmailToken connect(PromoterId promoterId, String accessToken,
                                             String refreshToken, Instant tokenExpiry,
                                             String gmailAddress, Instant watchExpiry) {
        return new PromoterGmailToken(promoterId, accessToken, refreshToken,
                tokenExpiry, gmailAddress, watchExpiry, Instant.now());
    }

    public static PromoterGmailToken reconstitute(
            PromoterId promoterId, String accessToken, String refreshToken,
            Instant tokenExpiry, String gmailAddress, Instant watchExpiry,
            Instant connectedAt) {
        return new PromoterGmailToken(promoterId, accessToken, refreshToken,
                tokenExpiry, gmailAddress, watchExpiry, connectedAt);
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public PromoterId getPromoterId()  { return promoterId; }
    public String getAccessToken()     { return accessToken; }
    public String getRefreshToken()    { return refreshToken; }
    public Instant getTokenExpiry()    { return tokenExpiry; }
    public String getGmailAddress()    { return gmailAddress; }
    public Instant getWatchExpiry()    { return watchExpiry; }
    public Instant getConnectedAt()    { return connectedAt; }
}
