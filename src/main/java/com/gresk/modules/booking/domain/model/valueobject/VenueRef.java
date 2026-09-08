package com.gresk.modules.booking.domain.model.valueobject;

import java.util.UUID;

/**
 * Referencia débil a un recinto/venue. No existe (todavía) un aggregate {@code Venue}
 * propio en el sistema, así que {@code venueId} es opcional y {@code venueName}/{@code territory}
 * van denormalizados en el propio Booking, igual patrón que {@code Contract.linkedEventId}.
 */
public record VenueRef(UUID venueId, String venueName, Territory territory) {

    public VenueRef {
        if (venueName == null || venueName.isBlank()) {
            throw new IllegalArgumentException("VenueRef venueName must not be blank");
        }
        if (territory == null) {
            throw new IllegalArgumentException("VenueRef territory must not be null");
        }
    }

    public static VenueRef of(UUID venueId, String venueName, Territory territory) {
        return new VenueRef(venueId, venueName, territory);
    }
}
