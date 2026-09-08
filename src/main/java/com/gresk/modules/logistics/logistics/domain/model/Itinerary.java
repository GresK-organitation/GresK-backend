package com.gresk.modules.logistics.domain.model;

import com.gresk.modules.logistics.domain.model.valueobject.ItinerarySegment;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Aggregate root de los tramos de transporte (vuelos, trenes, transfers) de un Tour.
 * Modelo inspirado en TripIt: una lista de segmentos tipados que se ordena
 * cronológicamente para construir la línea de tiempo del Tour Book.
 */
public final class Itinerary {

    private final ItineraryId id;
    private final TourId tourId;
    private final PromoterId promoterId;
    private final Instant createdAt;

    private List<ItinerarySegment> segments;
    private Instant updatedAt;

    private Itinerary(ItineraryId id, TourId tourId, PromoterId promoterId, Instant createdAt,
                       List<ItinerarySegment> segments, Instant updatedAt) {
        this.id = id;
        this.tourId = tourId;
        this.promoterId = promoterId;
        this.createdAt = createdAt;
        this.segments = segments != null ? new ArrayList<>(segments) : new ArrayList<>();
        this.updatedAt = updatedAt;
    }

    public static Itinerary create(TourId tourId, PromoterId promoterId) {
        Instant now = Instant.now();
        return new Itinerary(ItineraryId.generate(), tourId, promoterId, now, List.of(), now);
    }

    public static Itinerary reconstitute(ItineraryId id, TourId tourId, PromoterId promoterId, Instant createdAt,
                                          List<ItinerarySegment> segments, Instant updatedAt) {
        return new Itinerary(id, tourId, promoterId, createdAt, segments, updatedAt);
    }

    /** Reemplaza todos los tramos a la vez (altas, bajas y reasignación de viajeros). */
    public void replaceSegments(List<ItinerarySegment> newSegments) {
        this.segments = new ArrayList<>(newSegments == null ? List.of() : newSegments);
        this.segments.sort(Comparator.comparing(ItinerarySegment::departureAt));
        this.updatedAt = Instant.now();
    }

    public ItineraryId getId() { return id; }
    public TourId getTourId() { return tourId; }
    public PromoterId getPromoterId() { return promoterId; }
    public Instant getCreatedAt() { return createdAt; }
    public List<ItinerarySegment> getSegments() { return List.copyOf(segments); }
    public Instant getUpdatedAt() { return updatedAt; }
}
