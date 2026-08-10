package com.gresk.modules.email.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Versión de rider detectada para un evento, normalmente derivada de un
 * correo (source_email_id). El par (eventId, versionNumber) es único.
 */
public final class EmailRiderVersion {

    private final EmailRiderVersionId id;
    private final UUID                eventId;
    private final int                 versionNumber;
    private final EmailMessageId      sourceEmailId;
    private final RiderVersionSource  createdBy;
    private final Instant             createdAt;

    private String riderDataJson;
    private String diffFromPrevJson;
    private String notes;

    private EmailRiderVersion(EmailRiderVersionId id, UUID eventId, int versionNumber,
                              EmailMessageId sourceEmailId, String riderDataJson,
                              String diffFromPrevJson, RiderVersionSource createdBy,
                              String notes, Instant createdAt) {
        this.id               = id;
        this.eventId          = eventId;
        this.versionNumber    = versionNumber;
        this.sourceEmailId    = sourceEmailId;
        this.riderDataJson    = riderDataJson;
        this.diffFromPrevJson = diffFromPrevJson;
        this.createdBy        = createdBy;
        this.notes            = notes;
        this.createdAt        = createdAt;
    }

    // ── Factories ─────────────────────────────────────────────────────────────

    public static EmailRiderVersion create(UUID eventId, int versionNumber,
                                           EmailMessageId sourceEmailId, String riderDataJson,
                                           String diffFromPrevJson, RiderVersionSource createdBy,
                                           String notes) {
        return new EmailRiderVersion(
                EmailRiderVersionId.generate(), eventId, versionNumber,
                sourceEmailId, riderDataJson, diffFromPrevJson,
                createdBy, notes, Instant.now()
        );
    }

    public static EmailRiderVersion reconstitute(
            EmailRiderVersionId id, UUID eventId, int versionNumber,
            EmailMessageId sourceEmailId, String riderDataJson,
            String diffFromPrevJson, RiderVersionSource createdBy,
            String notes, Instant createdAt) {
        return new EmailRiderVersion(id, eventId, versionNumber, sourceEmailId,
                riderDataJson, diffFromPrevJson, createdBy, notes, createdAt);
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public EmailRiderVersionId getId()         { return id; }
    public UUID getEventId()                   { return eventId; }
    public int getVersionNumber()              { return versionNumber; }
    public EmailMessageId getSourceEmailId()   { return sourceEmailId; }
    public String getRiderDataJson()           { return riderDataJson; }
    public String getDiffFromPrevJson()        { return diffFromPrevJson; }
    public RiderVersionSource getCreatedBy()   { return createdBy; }
    public String getNotes()                   { return notes; }
    public Instant getCreatedAt()              { return createdAt; }
}
