package com.gresk.modules.logistics.domain.model.valueobject;

import com.gresk.modules.logistics.domain.model.SegmentType;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * Tramo de transporte (vuelo, tren o transfer por carretera) de un Itinerary.
 * Modelo plano con un único tipo y campos opcionales según SegmentType, igual
 * patrón que booking.VenueRef/TerritorialExclusivity. travelerIds referencia
 * TravelPartyMember.id: no todo el travel party viaja en todos los tramos.
 */
public record ItinerarySegment(
        UUID id,
        SegmentType type,
        Instant departureAt,
        String departureLocation,
        Instant arrivalAt,
        String arrivalLocation,
        String carrierOrOperator,
        String segmentCode,
        String confirmationReference,
        String seatOrCapacityInfo,
        String voucherUrl,
        String notes,
        Set<UUID> travelerIds
) {

    public ItinerarySegment {
        if (id == null) id = UUID.randomUUID();
        if (type == null) throw new IllegalArgumentException("ItinerarySegment type must not be null");
        if (departureAt == null) throw new IllegalArgumentException("ItinerarySegment departureAt must not be null");
        if (departureLocation == null || departureLocation.isBlank()) {
            throw new IllegalArgumentException("ItinerarySegment departureLocation must not be blank");
        }
        if (arrivalLocation == null || arrivalLocation.isBlank()) {
            throw new IllegalArgumentException("ItinerarySegment arrivalLocation must not be blank");
        }
        if (arrivalAt != null && arrivalAt.isBefore(departureAt)) {
            throw new IllegalArgumentException("ItinerarySegment arrivalAt cannot precede departureAt");
        }
        travelerIds = travelerIds == null ? Set.of() : Set.copyOf(travelerIds);
    }
}
