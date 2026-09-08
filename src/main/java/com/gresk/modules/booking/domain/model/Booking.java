package com.gresk.modules.booking.domain.model;

import com.gresk.modules.booking.domain.exception.HoldExpirationRequiredException;
import com.gresk.modules.booking.domain.exception.InvalidBookingException;
import com.gresk.modules.booking.domain.exception.InvalidBookingStatusTransitionException;
import com.gresk.modules.booking.domain.exception.MilestoneNotFoundException;
import com.gresk.modules.booking.domain.model.valueobject.DaySheet;
import com.gresk.modules.booking.domain.model.valueobject.Milestone;
import com.gresk.modules.booking.domain.model.valueobject.MilestoneBlueprint;
import com.gresk.modules.booking.domain.model.valueobject.TerritorialExclusivity;
import com.gresk.modules.booking.domain.model.valueobject.VenueRef;
import com.gresk.modules.booking.domain.service.MilestoneEngine;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate root de una reserva (hold) de un artista en un venue/fecha, con su
 * máquina de estados ({@link BookingStatus}), sus hitos automáticos ({@link Milestone})
 * y, opcionalmente, su day sheet y su cláusula de exclusividad territorial.
 */
public final class Booking {

    private final BookingId id;
    private final PromoterId promoterId;
    private final UUID artistId;
    private final Instant createdAt;

    private VenueRef venue;
    private Instant eventDate;
    private BookingStatus status;
    private Instant holdExpiresAt;
    private List<Milestone> milestones;
    private DaySheet daySheet;
    private TerritorialExclusivity exclusivity;
    private UUID linkedEventId;
    private UUID linkedContractId;
    private String notes;
    private Instant updatedAt;

    private Booking(BookingId id, PromoterId promoterId, UUID artistId, Instant createdAt,
                     VenueRef venue, Instant eventDate, BookingStatus status, Instant holdExpiresAt,
                     List<Milestone> milestones, DaySheet daySheet, TerritorialExclusivity exclusivity,
                     UUID linkedEventId, UUID linkedContractId, String notes, Instant updatedAt) {
        this.id = id;
        this.promoterId = promoterId;
        this.artistId = artistId;
        this.createdAt = createdAt;
        this.venue = venue;
        this.eventDate = eventDate;
        this.status = status;
        this.holdExpiresAt = holdExpiresAt;
        this.milestones = milestones != null ? new ArrayList<>(milestones) : new ArrayList<>();
        this.daySheet = daySheet;
        this.exclusivity = exclusivity;
        this.linkedEventId = linkedEventId;
        this.linkedContractId = linkedContractId;
        this.notes = notes;
        this.updatedAt = updatedAt;
    }

    // ── Factories ────────────────────────────────────────────────────────────

    public static Booking create(PromoterId promoterId, UUID artistId, VenueRef venue, Instant eventDate,
                                  BookingStatus initialStatus, Instant holdExpiresAt,
                                  List<MilestoneBlueprint> milestoneBlueprints,
                                  TerritorialExclusivity exclusivity, String notes) {
        validateCore(artistId, venue, eventDate);
        if (initialStatus != BookingStatus.HOLD_1 && initialStatus != BookingStatus.HOLD_2) {
            throw new InvalidBookingException("A booking can only be created with status HOLD_1 or HOLD_2");
        }
        validateHoldExpiration(initialStatus, holdExpiresAt);

        Instant now = Instant.now();
        List<Milestone> milestones = MilestoneEngine.generate(eventDate, milestoneBlueprints);
        return new Booking(BookingId.generate(), promoterId, artistId, now, venue, eventDate, initialStatus,
                holdExpiresAt, milestones, null, exclusivity, null, null, notes, now);
    }

    public static Booking reconstitute(BookingId id, PromoterId promoterId, UUID artistId, Instant createdAt,
                                        VenueRef venue, Instant eventDate, BookingStatus status, Instant holdExpiresAt,
                                        List<Milestone> milestones, DaySheet daySheet, TerritorialExclusivity exclusivity,
                                        UUID linkedEventId, UUID linkedContractId, String notes, Instant updatedAt) {
        return new Booking(id, promoterId, artistId, createdAt, venue, eventDate, status, holdExpiresAt,
                milestones, daySheet, exclusivity, linkedEventId, linkedContractId, notes, updatedAt);
    }

    // ── Behavior ─────────────────────────────────────────────────────────────

    public void promoteToHold2(Instant newHoldExpiresAt) {
        requireTransition(BookingStatus.HOLD_2);
        if (newHoldExpiresAt == null || !newHoldExpiresAt.isAfter(Instant.now())) {
            throw new HoldExpirationRequiredException();
        }
        this.status = BookingStatus.HOLD_2;
        this.holdExpiresAt = newHoldExpiresAt;
        touch();
    }

    public void confirm() {
        requireTransition(BookingStatus.CONFIRMED);
        this.status = BookingStatus.CONFIRMED;
        this.holdExpiresAt = null;
        touch();
    }

    public void cancel(String reason) {
        requireTransition(BookingStatus.CANCELLED);
        this.status = BookingStatus.CANCELLED;
        this.notes = appendNote(reason);
        touch();
    }

    public void expire() {
        requireTransition(BookingStatus.EXPIRED);
        this.status = BookingStatus.EXPIRED;
        touch();
    }

    public void reschedule(Instant newEventDate) {
        if (status.isTerminal()) {
            throw new InvalidBookingException("Cannot reschedule a booking in terminal status " + status);
        }
        if (newEventDate == null) {
            throw new InvalidBookingException("newEventDate must not be null");
        }
        this.eventDate = newEventDate;
        this.milestones = new ArrayList<>(MilestoneEngine.recalculateAll(newEventDate, milestones));
        if (this.daySheet != null) {
            this.daySheet = this.daySheet.retarget(newEventDate.atZone(java.time.ZoneOffset.UTC).toLocalDate());
        }
        touch();
    }

    public void completeMilestone(UUID milestoneId, String notes) {
        int idx = findMilestoneIndex(milestoneId);
        milestones.set(idx, milestones.get(idx).complete(notes));
        touch();
    }

    public void skipMilestone(UUID milestoneId, String reason) {
        int idx = findMilestoneIndex(milestoneId);
        milestones.set(idx, milestones.get(idx).skip(reason));
        touch();
    }

    public void assignDaySheet(DaySheet daySheet) {
        if (status.isTerminal()) {
            throw new InvalidBookingException("Cannot assign a day sheet to a booking in terminal status " + status);
        }
        this.daySheet = daySheet;
        touch();
    }

    public void applyExclusivity(TerritorialExclusivity exclusivity) {
        this.exclusivity = exclusivity;
        touch();
    }

    public void withLinkedEventId(UUID linkedEventId) {
        this.linkedEventId = linkedEventId;
        touch();
    }

    public void withLinkedContractId(UUID linkedContractId) {
        this.linkedContractId = linkedContractId;
        touch();
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private void requireTransition(BookingStatus target) {
        if (!status.canTransitionTo(target)) {
            throw new InvalidBookingStatusTransitionException(status, target);
        }
    }

    private int findMilestoneIndex(UUID milestoneId) {
        for (int i = 0; i < milestones.size(); i++) {
            if (milestones.get(i).milestoneId().equals(milestoneId)) return i;
        }
        throw new MilestoneNotFoundException(milestoneId.toString());
    }

    private String appendNote(String reason) {
        if (reason == null || reason.isBlank()) return notes;
        return notes == null || notes.isBlank() ? reason : notes + " | " + reason;
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    private static void validateCore(UUID artistId, VenueRef venue, Instant eventDate) {
        if (artistId == null) {
            throw new InvalidBookingException("artistId must not be null");
        }
        if (venue == null) {
            throw new InvalidBookingException("venue must not be null");
        }
        if (eventDate == null) {
            throw new InvalidBookingException("eventDate must not be null");
        }
    }

    private static void validateHoldExpiration(BookingStatus initialStatus, Instant holdExpiresAt) {
        if (initialStatus.isHold() && (holdExpiresAt == null || !holdExpiresAt.isAfter(Instant.now()))) {
            throw new HoldExpirationRequiredException();
        }
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public BookingId getId() { return id; }
    public PromoterId getPromoterId() { return promoterId; }
    public UUID getArtistId() { return artistId; }
    public Instant getCreatedAt() { return createdAt; }
    public VenueRef getVenue() { return venue; }
    public Instant getEventDate() { return eventDate; }
    public BookingStatus getStatus() { return status; }
    public Instant getHoldExpiresAt() { return holdExpiresAt; }
    public List<Milestone> getMilestones() { return List.copyOf(milestones); }
    public DaySheet getDaySheet() { return daySheet; }
    public TerritorialExclusivity getExclusivity() { return exclusivity; }
    public UUID getLinkedEventId() { return linkedEventId; }
    public UUID getLinkedContractId() { return linkedContractId; }
    public String getNotes() { return notes; }
    public Instant getUpdatedAt() { return updatedAt; }
}
