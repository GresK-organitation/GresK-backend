package com.gresk.modules.agenda.infrastructure.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "calendar_sync_accounts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarSyncAccountEntity {

    @Id
    private UUID id;

    @Column(name = "promoter_id", nullable = false)
    private UUID promoterId;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Column(name = "external_account_email")
    private String externalAccountEmail;

    @Column(name = "access_token_ciphertext", columnDefinition = "TEXT")
    private String accessTokenCiphertext;

    @Column(name = "refresh_token_ciphertext", columnDefinition = "TEXT")
    private String refreshTokenCiphertext;

    @Column(name = "token_expiry")
    private Instant tokenExpiry;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "sync_token", columnDefinition = "TEXT")
    private String syncToken;

    @Column(name = "last_synced_at")
    private Instant lastSyncedAt;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    @Column(name = "watch_channel_id")
    private String watchChannelId;

    @Column(name = "watch_resource_id")
    private String watchResourceId;

    @Column(name = "watch_expiry")
    private Instant watchExpiry;

    @Column(name = "ms_subscription_id")
    private String msSubscriptionId;

    @Column(name = "ms_subscription_expiry")
    private Instant msSubscriptionExpiry;

    @Column(name = "client_state_secret", nullable = false)
    private String clientStateSecret;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalendarEventMappingEntity> mappings = new ArrayList<>();
}
