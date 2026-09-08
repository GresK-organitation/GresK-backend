package com.gresk.modules.show.domain.model;

import com.gresk.modules.show.domain.model.valueobject.LogEntryType;
import com.gresk.shared.domain.valueobject.AssetId;

import java.time.Instant;
import java.util.List;

/**
 * Entrada de la bitácora (trazabilidad) del show: registro cronológico append-only de
 * comunicaciones, decisiones, acuerdos e incidencias. Nunca se edita ni se borra, igual que
 * {@code contract.AuditTrailEntry}; una corrección se registra como una nueva entrada.
 */
public final class ShowLogEntry {

    private final ShowLogEntryId id;
    private final ShowId         showId;
    private final LogEntryType   type;
    private final String         actor;
    private final Instant        occurredAt;
    private final String         description;
    private final String         relatedParty;      // p.ej. "Booking agency X", "Ayuntamiento" (nullable)
    private final List<AssetId>  attachments;

    private ShowLogEntry(ShowLogEntryId id, ShowId showId, LogEntryType type, String actor, Instant occurredAt,
                          String description, String relatedParty, List<AssetId> attachments) {
        this.id           = id;
        this.showId       = showId;
        this.type         = type;
        this.actor        = actor;
        this.occurredAt   = occurredAt;
        this.description  = description;
        this.relatedParty = relatedParty;
        this.attachments  = attachments != null ? List.copyOf(attachments) : List.of();
    }

    public static ShowLogEntry record(ShowId showId, LogEntryType type, String actor, String description,
                                       String relatedParty, List<AssetId> attachments) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("ShowLogEntry description must not be blank");
        }
        return new ShowLogEntry(ShowLogEntryId.generate(), showId, type, actor, Instant.now(),
                description, relatedParty, attachments);
    }

    public static ShowLogEntry statusChange(ShowId showId, String actor, ShowStatus from, ShowStatus to) {
        return record(showId, LogEntryType.STATUS_CHANGE, actor,
                "Cambio de estado: " + from + " -> " + to, null, List.of());
    }

    public static ShowLogEntry reconstitute(ShowLogEntryId id, ShowId showId, LogEntryType type, String actor,
                                             Instant occurredAt, String description, String relatedParty,
                                             List<AssetId> attachments) {
        return new ShowLogEntry(id, showId, type, actor, occurredAt, description, relatedParty, attachments);
    }

    public ShowLogEntryId getId()          { return id; }
    public ShowId         getShowId()      { return showId; }
    public LogEntryType   getType()        { return type; }
    public String         getActor()       { return actor; }
    public Instant        getOccurredAt()  { return occurredAt; }
    public String         getDescription() { return description; }
    public String         getRelatedParty(){ return relatedParty; }
    public List<AssetId>  getAttachments() { return attachments; }
}
