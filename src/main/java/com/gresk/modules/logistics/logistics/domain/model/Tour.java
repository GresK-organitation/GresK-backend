package com.gresk.modules.logistics.domain.model;

import com.gresk.modules.logistics.domain.exception.InvalidTourException;
import com.gresk.modules.logistics.domain.model.valueobject.EmergencyContact;
import com.gresk.modules.logistics.domain.model.valueobject.PointOfInterest;
import com.gresk.modules.logistics.domain.model.valueobject.TourLeg;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate root de una gira (una o varias fechas/Booking consecutivas) de un artista.
 * Ancla el resto del módulo de logística (TravelParty, Itinerary, RoomingList), que lo
 * referencian por tourId en vez de anidarse aquí, mismo patrón que Show/TechnicalRider.
 */
public final class Tour {

    private final TourId id;
    private final PromoterId promoterId;
    private final UUID artistId;
    private final Instant createdAt;

    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private TourStatus status;
    private List<TourLeg> legs;
    private List<EmergencyContact> emergencyContacts;
    private List<PointOfInterest> pointsOfInterest;
    private String notes;
    private Instant updatedAt;

    private Tour(TourId id, PromoterId promoterId, UUID artistId, Instant createdAt, String name,
                 LocalDate startDate, LocalDate endDate, TourStatus status, List<TourLeg> legs,
                 List<EmergencyContact> emergencyContacts, List<PointOfInterest> pointsOfInterest,
                 String notes, Instant updatedAt) {
        this.id = id;
        this.promoterId = promoterId;
        this.artistId = artistId;
        this.createdAt = createdAt;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.legs = legs != null ? new ArrayList<>(legs) : new ArrayList<>();
        this.emergencyContacts = emergencyContacts != null ? new ArrayList<>(emergencyContacts) : new ArrayList<>();
        this.pointsOfInterest = pointsOfInterest != null ? new ArrayList<>(pointsOfInterest) : new ArrayList<>();
        this.notes = notes;
        this.updatedAt = updatedAt;
    }

    // ── Factories ────────────────────────────────────────────────────────────

    public static Tour create(PromoterId promoterId, UUID artistId, String name, LocalDate startDate,
                               LocalDate endDate, String notes) {
        validateCore(artistId, name, startDate, endDate);
        Instant now = Instant.now();
        return new Tour(TourId.generate(), promoterId, artistId, now, name.trim(), startDate, endDate,
                TourStatus.PLANNED, List.of(), List.of(), List.of(), notes, now);
    }

    public static Tour reconstitute(TourId id, PromoterId promoterId, UUID artistId, Instant createdAt, String name,
                                     LocalDate startDate, LocalDate endDate, TourStatus status, List<TourLeg> legs,
                                     List<EmergencyContact> emergencyContacts, List<PointOfInterest> pointsOfInterest,
                                     String notes, Instant updatedAt) {
        return new Tour(id, promoterId, artistId, createdAt, name, startDate, endDate, status, legs,
                emergencyContacts, pointsOfInterest, notes, updatedAt);
    }

    // ── Behavior ─────────────────────────────────────────────────────────────

    public void updateDetails(String name, LocalDate startDate, LocalDate endDate, String notes) {
        if (status.isTerminal()) {
            throw new InvalidTourException("Cannot update a tour in terminal status " + status);
        }
        validateCore(artistId, name, startDate, endDate);
        this.name = name.trim();
        this.startDate = startDate;
        this.endDate = endDate;
        this.notes = notes;
        touch();
    }

    public void replaceLegs(List<TourLeg> newLegs) {
        this.legs = new ArrayList<>(newLegs == null ? List.of() : newLegs);
        this.legs.sort(Comparator.comparingInt(TourLeg::sequenceOrder));
        touch();
    }

    public void replaceEmergencyContacts(List<EmergencyContact> newContacts) {
        this.emergencyContacts = new ArrayList<>(newContacts == null ? List.of() : newContacts);
        touch();
    }

    public void replacePointsOfInterest(List<PointOfInterest> newPois) {
        this.pointsOfInterest = new ArrayList<>(newPois == null ? List.of() : newPois);
        touch();
    }

    public void activate() {
        requireTransition(TourStatus.ACTIVE);
        this.status = TourStatus.ACTIVE;
        touch();
    }

    public void complete() {
        requireTransition(TourStatus.COMPLETED);
        this.status = TourStatus.COMPLETED;
        touch();
    }

    public void cancel(String reason) {
        requireTransition(TourStatus.CANCELLED);
        this.status = TourStatus.CANCELLED;
        this.notes = appendNote(reason);
        touch();
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private void requireTransition(TourStatus target) {
        if (!status.canTransitionTo(target)) {
            throw new InvalidTourException("Cannot transition tour from " + status + " to " + target);
        }
    }

    private String appendNote(String reason) {
        if (reason == null || reason.isBlank()) return notes;
        return notes == null || notes.isBlank() ? reason : notes + " | " + reason;
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    private static void validateCore(UUID artistId, String name, LocalDate startDate, LocalDate endDate) {
        if (artistId == null) throw new InvalidTourException("artistId must not be null");
        if (name == null || name.isBlank()) throw new InvalidTourException("name must not be blank");
        if (startDate == null) throw new InvalidTourException("startDate must not be null");
        if (endDate == null) throw new InvalidTourException("endDate must not be null");
        if (endDate.isBefore(startDate)) throw new InvalidTourException("endDate cannot precede startDate");
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public TourId getId() { return id; }
    public PromoterId getPromoterId() { return promoterId; }
    public UUID getArtistId() { return artistId; }
    public Instant getCreatedAt() { return createdAt; }
    public String getName() { return name; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public TourStatus getStatus() { return status; }
    public List<TourLeg> getLegs() { return List.copyOf(legs); }
    public List<EmergencyContact> getEmergencyContacts() { return List.copyOf(emergencyContacts); }
    public List<PointOfInterest> getPointsOfInterest() { return List.copyOf(pointsOfInterest); }
    public String getNotes() { return notes; }
    public Instant getUpdatedAt() { return updatedAt; }
}
