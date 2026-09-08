package com.gresk.modules.show.domain.model.valueobject;

import com.gresk.modules.venue.domain.model.VenueId;

/**
 * Snapshot inmutable del recinto y aforo modular elegidos para el show. Se congela en el
 * momento de la selección (mismo motivo que {@code booking.VenueRef}): si más adelante el
 * promotor edita la ficha técnica del venue, un show ya confirmado no debe cambiar de aforo
 * bajo sus pies.
 */
public record VenueBooking(VenueId venueId, String venueName, String capacityConfigCode,
                            String capacityConfigLabel, int confirmedCapacity) {

    public VenueBooking {
        if (venueId == null) {
            throw new IllegalArgumentException("VenueBooking venueId must not be null");
        }
        if (venueName == null || venueName.isBlank()) {
            throw new IllegalArgumentException("VenueBooking venueName must not be blank");
        }
        if (capacityConfigCode == null || capacityConfigCode.isBlank()) {
            throw new IllegalArgumentException("VenueBooking capacityConfigCode must not be blank");
        }
        if (confirmedCapacity < 1) {
            throw new IllegalArgumentException("VenueBooking confirmedCapacity must be at least 1");
        }
    }
}
