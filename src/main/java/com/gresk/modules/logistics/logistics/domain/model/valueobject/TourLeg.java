package com.gresk.modules.logistics.domain.model.valueobject;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Referencia débil a una fecha/show ya reservado en booking.Booking. venueName/venueCity
 * van denormalizados para no depender de una consulta cross-módulo al listar el Tour,
 * mismo patrón que booking.VenueRef respecto a un futuro aggregate Venue.
 */
public record TourLeg(UUID bookingId, int sequenceOrder, LocalDate showDate, String venueName, String venueCity) {

    public TourLeg {
        if (bookingId == null) throw new IllegalArgumentException("TourLeg bookingId must not be null");
        if (showDate == null) throw new IllegalArgumentException("TourLeg showDate must not be null");
        if (venueName == null || venueName.isBlank()) {
            throw new IllegalArgumentException("TourLeg venueName must not be blank");
        }
    }

    public static TourLeg of(UUID bookingId, int sequenceOrder, LocalDate showDate, String venueName, String venueCity) {
        return new TourLeg(bookingId, sequenceOrder, showDate, venueName, venueCity);
    }
}
