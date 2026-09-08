package com.gresk.modules.logistics.domain.model;

import com.gresk.modules.logistics.domain.exception.InvalidTravelPartyException;
import com.gresk.modules.logistics.domain.model.valueobject.TravelPartyMember;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Aggregate root del roster de expedición (músicos + crew) de un Tour. Separado del
 * aggregate Tour porque su ciclo de edición (altas/bajas de personal, necesidades
 * individuales) es independiente y más frecuente, mismo patrón que TechnicalRider
 * respecto a Show.
 */
public final class TravelParty {

    private final TravelPartyId id;
    private final TourId tourId;
    private final PromoterId promoterId;
    private final Instant createdAt;

    private List<TravelPartyMember> members;
    private Instant updatedAt;

    private TravelParty(TravelPartyId id, TourId tourId, PromoterId promoterId, Instant createdAt,
                         List<TravelPartyMember> members, Instant updatedAt) {
        this.id = id;
        this.tourId = tourId;
        this.promoterId = promoterId;
        this.createdAt = createdAt;
        this.members = members != null ? new ArrayList<>(members) : new ArrayList<>();
        this.updatedAt = updatedAt;
    }

    public static TravelParty create(TourId tourId, PromoterId promoterId) {
        Instant now = Instant.now();
        return new TravelParty(TravelPartyId.generate(), tourId, promoterId, now, List.of(), now);
    }

    public static TravelParty reconstitute(TravelPartyId id, TourId tourId, PromoterId promoterId, Instant createdAt,
                                            List<TravelPartyMember> members, Instant updatedAt) {
        return new TravelParty(id, tourId, promoterId, createdAt, members, updatedAt);
    }

    /** Reemplaza el roster completo (altas, bajas y ediciones a la vez), igual patrón que booking.assignDaySheet. */
    public void replaceMembers(List<TravelPartyMember> newMembers) {
        List<TravelPartyMember> candidates = newMembers == null ? List.of() : newMembers;
        long distinctPeople = candidates.stream().map(TravelPartyMember::personRef).distinct().count();
        if (distinctPeople != candidates.size()) {
            throw new InvalidTravelPartyException("A person cannot appear twice in the same travel party");
        }
        this.members = new ArrayList<>(candidates);
        this.updatedAt = Instant.now();
    }

    public Optional<TravelPartyMember> findMember(UUID memberId) {
        return members.stream().filter(m -> m.id().equals(memberId)).findFirst();
    }

    public TravelPartyId getId() { return id; }
    public TourId getTourId() { return tourId; }
    public PromoterId getPromoterId() { return promoterId; }
    public Instant getCreatedAt() { return createdAt; }
    public List<TravelPartyMember> getMembers() { return List.copyOf(members); }
    public List<TravelPartyMember> getActiveMembers() { return members.stream().filter(TravelPartyMember::active).toList(); }
    public Instant getUpdatedAt() { return updatedAt; }
}
