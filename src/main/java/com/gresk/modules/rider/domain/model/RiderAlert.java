package com.gresk.modules.rider.domain.model;

import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.time.Instant;

public final class RiderAlert {

    private final AlertId   id;
    private final PromoterId promoterId;
    private final EventId   eventId;
    private final RiderId   riderId;
    private final String    message;
    private final Instant   createdAt;

    private boolean read;

    private RiderAlert(AlertId id, PromoterId promoterId, EventId eventId, RiderId riderId,
                       String message, boolean read, Instant createdAt) {
        this.id          = id;
        this.promoterId  = promoterId;
        this.eventId     = eventId;
        this.riderId     = riderId;
        this.message     = message;
        this.read        = read;
        this.createdAt   = createdAt;
    }

    // ── Factories ────────────────────────────────────────────────────────────

    public static RiderAlert create(PromoterId promoterId, EventId eventId, RiderId riderId, String message) {
        return new RiderAlert(AlertId.generate(), promoterId, eventId, riderId, message, false, Instant.now());
    }

    public static RiderAlert reconstitute(AlertId id, PromoterId promoterId, EventId eventId,
                                          RiderId riderId, String message, boolean read, Instant createdAt) {
        return new RiderAlert(id, promoterId, eventId, riderId, message, read, createdAt);
    }

    // ── Behavior ─────────────────────────────────────────────────────────────

    public void markRead() {
        this.read = true;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public AlertId    getId()         { return id; }
    public PromoterId getPromoterId() { return promoterId; }
    public EventId    getEventId()    { return eventId; }
    public RiderId    getRiderId()    { return riderId; }
    public String     getMessage()   { return message; }
    public boolean    isRead()        { return read; }
    public Instant    getCreatedAt()  { return createdAt; }
}
