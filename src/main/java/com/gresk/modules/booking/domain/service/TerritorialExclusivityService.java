package com.gresk.modules.booking.domain.service;

import com.gresk.modules.booking.domain.model.Booking;
import com.gresk.modules.booking.domain.model.valueobject.TerritorialExclusivity;
import com.gresk.modules.booking.domain.model.valueobject.VenueRef;

import java.time.Instant;
import java.util.List;

/**
 * Detecta conflictos de exclusividad territorial ("radius clause") entre una reserva
 * candidata y las reservas activas existentes del mismo artista. Servicio de dominio
 * sin estado; comprueba el solapamiento en ambas direcciones (la exclusividad del
 * candidato y la de cada booking existente, si la tiene).
 */
public final class TerritorialExclusivityService {

    private TerritorialExclusivityService() {
    }

    public static List<Booking> findConflicts(VenueRef candidateVenue,
                                                Instant candidateEventDate,
                                                TerritorialExclusivity candidateExclusivity,
                                                List<Booking> otherActiveBookingsForArtist) {
        if (otherActiveBookingsForArtist == null || otherActiveBookingsForArtist.isEmpty()) {
            return List.of();
        }
        return otherActiveBookingsForArtist.stream()
                .filter(existing -> conflicts(candidateVenue, candidateEventDate, candidateExclusivity, existing))
                .toList();
    }

    private static boolean conflicts(VenueRef candidateVenue, Instant candidateEventDate,
                                      TerritorialExclusivity candidateExclusivity, Booking existing) {
        if (candidateExclusivity != null
                && candidateExclusivity.protectedTerritory().overlaps(existing.getVenue().territory())
                && candidateExclusivity.windowOverlaps(candidateEventDate, existing.getEventDate())) {
            return true;
        }
        TerritorialExclusivity existingExclusivity = existing.getExclusivity();
        return existingExclusivity != null
                && existingExclusivity.protectedTerritory().overlaps(candidateVenue.territory())
                && existingExclusivity.windowOverlaps(existing.getEventDate(), candidateEventDate);
    }
}
