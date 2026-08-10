package com.gresk.modules.email.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "promoter_gmail_tokens")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoterGmailTokenEntity {

    @Id
    @Column(name = "promoter_id", columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID promoterId;

    // Se persiste en claro en esta fase; cifrado AES-256-GCM en el Issue #2 del EIE
    @Column(name = "access_token", nullable = false, columnDefinition = "TEXT")
    private String accessToken;

    @Column(name = "refresh_token", nullable = false, columnDefinition = "TEXT")
    private String refreshToken;

    @Column(name = "token_expiry")
    private Instant tokenExpiry;

    @Column(name = "gmail_address", length = 300)
    private String gmailAddress;

    @Column(name = "watch_expiry")
    private Instant watchExpiry;

    @Column(name = "last_history_id")
    private Long lastHistoryId;

    @Column(name = "connected_at", nullable = false, updatable = false)
    private Instant connectedAt;

    @PrePersist
    protected void onCreate() {
        if (connectedAt == null) connectedAt = Instant.now();
    }
}
