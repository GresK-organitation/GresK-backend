package com.gresk.modules.agenda.domain.model;

import com.gresk.modules.agenda.domain.exception.CalendarSyncNotConnectedException;
import com.gresk.modules.agenda.domain.exception.InvalidCalendarSyncStateException;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Cuenta de sincronización de calendario externo (Google Calendar / Outlook) de un promotor.
 * El sync verdaderamente bidireccional solo aplica a {@link LocalEntryType#AGENDA_ENTRY};
 * {@link LocalEntryType#BOOKING} es push-only (ver documentación del módulo booking).
 */
public final class CalendarSyncAccount {

    private final CalendarSyncAccountId id;
    private final PromoterId promoterId;
    private final CalendarProvider provider;
    private final Instant createdAt;

    private String externalAccountEmail;
    private OAuthTokenRef tokenRef;
    private SyncStatus status;
    private String syncToken;
    private Instant lastSyncedAt;
    private String lastError;
    private String watchChannelId;
    private String watchResourceId;
    private Instant watchExpiry;
    private String msSubscriptionId;
    private Instant msSubscriptionExpiry;
    private final String clientStateSecret;
    private List<CalendarEventMapping> mappings;
    private Instant updatedAt;

    private CalendarSyncAccount(CalendarSyncAccountId id, PromoterId promoterId, CalendarProvider provider,
                                 Instant createdAt, String externalAccountEmail, OAuthTokenRef tokenRef,
                                 SyncStatus status, String syncToken, Instant lastSyncedAt, String lastError,
                                 String watchChannelId, String watchResourceId, Instant watchExpiry,
                                 String msSubscriptionId, Instant msSubscriptionExpiry, String clientStateSecret,
                                 List<CalendarEventMapping> mappings, Instant updatedAt) {
        this.id = id;
        this.promoterId = promoterId;
        this.provider = provider;
        this.createdAt = createdAt;
        this.externalAccountEmail = externalAccountEmail;
        this.tokenRef = tokenRef;
        this.status = status;
        this.syncToken = syncToken;
        this.lastSyncedAt = lastSyncedAt;
        this.lastError = lastError;
        this.watchChannelId = watchChannelId;
        this.watchResourceId = watchResourceId;
        this.watchExpiry = watchExpiry;
        this.msSubscriptionId = msSubscriptionId;
        this.msSubscriptionExpiry = msSubscriptionExpiry;
        this.clientStateSecret = clientStateSecret;
        this.mappings = mappings != null ? new ArrayList<>(mappings) : new ArrayList<>();
        this.updatedAt = updatedAt;
    }

    // ── Factories ────────────────────────────────────────────────────────────

    public static CalendarSyncAccount connect(PromoterId promoterId, CalendarProvider provider,
                                                String externalAccountEmail, OAuthTokenRef tokenRef,
                                                String clientStateSecret) {
        if (provider == null) {
            throw new InvalidCalendarSyncStateException("provider must not be null");
        }
        if (clientStateSecret == null || clientStateSecret.isBlank()) {
            throw new InvalidCalendarSyncStateException("clientStateSecret must not be blank");
        }
        Instant now = Instant.now();
        return new CalendarSyncAccount(CalendarSyncAccountId.generate(), promoterId, provider, now,
                externalAccountEmail, tokenRef, SyncStatus.CONNECTED, null, null, null,
                null, null, null, null, null, clientStateSecret, List.of(), now);
    }

    public static CalendarSyncAccount reconstitute(CalendarSyncAccountId id, PromoterId promoterId,
                                                     CalendarProvider provider, Instant createdAt,
                                                     String externalAccountEmail, OAuthTokenRef tokenRef,
                                                     SyncStatus status, String syncToken, Instant lastSyncedAt,
                                                     String lastError, String watchChannelId, String watchResourceId,
                                                     Instant watchExpiry, String msSubscriptionId,
                                                     Instant msSubscriptionExpiry, String clientStateSecret,
                                                     List<CalendarEventMapping> mappings, Instant updatedAt) {
        return new CalendarSyncAccount(id, promoterId, provider, createdAt, externalAccountEmail, tokenRef, status,
                syncToken, lastSyncedAt, lastError, watchChannelId, watchResourceId, watchExpiry, msSubscriptionId,
                msSubscriptionExpiry, clientStateSecret, mappings, updatedAt);
    }

    // ── Behavior ─────────────────────────────────────────────────────────────

    public void disconnect() {
        this.status = SyncStatus.DISCONNECTED;
        this.tokenRef = null;
        this.syncToken = null;
        touch();
    }

    public void recordSuccessfulSync(String newSyncToken, Instant syncedAt) {
        if (status != SyncStatus.CONNECTED) {
            throw new CalendarSyncNotConnectedException(id.toString());
        }
        this.syncToken = newSyncToken;
        this.lastSyncedAt = syncedAt;
        this.lastError = null;
        touch();
    }

    public void recordSyncError(String errorMessage) {
        this.status = SyncStatus.ERROR;
        this.lastError = errorMessage;
        touch();
    }

    public void markTokenExpired() {
        this.status = SyncStatus.TOKEN_EXPIRED;
        touch();
    }

    public void reconnect() {
        this.status = SyncStatus.CONNECTED;
        this.lastError = null;
        touch();
    }

    /** Cierra el gap detectado en la integración Gmail: persiste el token refrescado tras cada uso. */
    public void updateTokenRef(OAuthTokenRef newTokenRef) {
        this.tokenRef = newTokenRef;
        touch();
    }

    public void registerGoogleWatch(String channelId, String resourceId, Instant expiry) {
        this.watchChannelId = channelId;
        this.watchResourceId = resourceId;
        this.watchExpiry = expiry;
        touch();
    }

    public void registerOutlookSubscription(String subscriptionId, Instant expiry) {
        this.msSubscriptionId = subscriptionId;
        this.msSubscriptionExpiry = expiry;
        touch();
    }

    public void upsertMapping(CalendarEventMapping mapping) {
        int idx = -1;
        for (int i = 0; i < mappings.size(); i++) {
            CalendarEventMapping m = mappings.get(i);
            if (m.localEntryId().equals(mapping.localEntryId()) && m.localType() == mapping.localType()) {
                idx = i;
                break;
            }
        }
        if (idx >= 0) {
            mappings.set(idx, mapping);
        } else {
            mappings.add(mapping);
        }
        touch();
    }

    public Optional<CalendarEventMapping> findMapping(UUID localEntryId, LocalEntryType type) {
        return mappings.stream()
                .filter(m -> m.localEntryId().equals(localEntryId) && m.localType() == type)
                .findFirst();
    }

    public Optional<CalendarEventMapping> findMappingByExternalId(String externalEventId) {
        return mappings.stream()
                .filter(m -> m.externalEventId().equals(externalEventId))
                .findFirst();
    }

    public void removeMapping(CalendarEventMapping mapping) {
        mappings.remove(mapping);
        touch();
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public CalendarSyncAccountId getId() { return id; }
    public PromoterId getPromoterId() { return promoterId; }
    public CalendarProvider getProvider() { return provider; }
    public Instant getCreatedAt() { return createdAt; }
    public String getExternalAccountEmail() { return externalAccountEmail; }
    public OAuthTokenRef getTokenRef() { return tokenRef; }
    public SyncStatus getStatus() { return status; }
    public String getSyncToken() { return syncToken; }
    public Instant getLastSyncedAt() { return lastSyncedAt; }
    public String getLastError() { return lastError; }
    public String getWatchChannelId() { return watchChannelId; }
    public String getWatchResourceId() { return watchResourceId; }
    public Instant getWatchExpiry() { return watchExpiry; }
    public String getMsSubscriptionId() { return msSubscriptionId; }
    public Instant getMsSubscriptionExpiry() { return msSubscriptionExpiry; }
    public String getClientStateSecret() { return clientStateSecret; }
    public List<CalendarEventMapping> getMappings() { return List.copyOf(mappings); }
    public Instant getUpdatedAt() { return updatedAt; }
}
