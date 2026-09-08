package com.gresk.modules.booking.domain.service;

import com.gresk.modules.booking.domain.model.Booking;
import com.gresk.modules.booking.domain.model.BookingStatus;
import com.gresk.modules.booking.domain.model.valueobject.Territory;
import com.gresk.modules.booking.domain.model.valueobject.TerritorialExclusivity;
import com.gresk.modules.booking.domain.model.valueobject.VenueRef;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TerritorialExclusivityServiceTest {

    private final PromoterId promoterId = PromoterId.generate();
    private final UUID artistId = UUID.randomUUID();

    @Test
    void detectaSolapamientoCuandoLaFechaCandidataCaeDentroDeLaVentanaDelBookingExistente() {
        Instant existingEventDate = Instant.parse("2026-06-15T20:00:00Z");
        Territory madrid = new Territory("España", "Madrid", "Madrid", null);
        VenueRef existingVenue = new VenueRef(null, "Wizink Center", madrid);
        TerritorialExclusivity existingExclusivity = new TerritorialExclusivity(madrid, 15, 15);

        Booking existingBooking = Booking.create(promoterId, artistId, existingVenue, existingEventDate,
                BookingStatus.HOLD_1, Instant.now().plus(1, ChronoUnit.DAYS), List.of(), existingExclusivity, null);

        Instant candidateEventDate = existingEventDate.plus(10, ChronoUnit.DAYS);
        VenueRef candidateVenue = new VenueRef(null, "La Riviera", madrid);

        List<Booking> conflicts = TerritorialExclusivityService.findConflicts(
                candidateVenue, candidateEventDate, null, List.of(existingBooking));

        assertEquals(1, conflicts.size());
        assertEquals(existingBooking.getId(), conflicts.get(0).getId());
    }

    @Test
    void noHayFalsoPositivoFueraDeLaVentanaDeExclusividad() {
        Instant existingEventDate = Instant.parse("2026-06-15T20:00:00Z");
        Territory madrid = new Territory("España", "Madrid", "Madrid", null);
        VenueRef existingVenue = new VenueRef(null, "Wizink Center", madrid);
        TerritorialExclusivity existingExclusivity = new TerritorialExclusivity(madrid, 15, 15);

        Booking existingBooking = Booking.create(promoterId, artistId, existingVenue, existingEventDate,
                BookingStatus.HOLD_1, Instant.now().plus(1, ChronoUnit.DAYS), List.of(), existingExclusivity, null);

        Instant candidateEventDate = existingEventDate.plus(60, ChronoUnit.DAYS);
        VenueRef candidateVenue = new VenueRef(null, "La Riviera", madrid);

        List<Booking> conflicts = TerritorialExclusivityService.findConflicts(
                candidateVenue, candidateEventDate, null, List.of(existingBooking));

        assertTrue(conflicts.isEmpty());
    }

    @Test
    void noHaySolapamientoEnCiudadesDistintasSinRadio() {
        Instant existingEventDate = Instant.parse("2026-06-15T20:00:00Z");
        Territory madrid = new Territory("España", "Madrid", "Madrid", null);
        Territory barcelona = new Territory("España", "Cataluña", "Barcelona", null);
        VenueRef existingVenue = new VenueRef(null, "Wizink Center", madrid);
        TerritorialExclusivity existingExclusivity = new TerritorialExclusivity(madrid, 15, 15);

        Booking existingBooking = Booking.create(promoterId, artistId, existingVenue, existingEventDate,
                BookingStatus.HOLD_1, Instant.now().plus(1, ChronoUnit.DAYS), List.of(), existingExclusivity, null);

        VenueRef candidateVenue = new VenueRef(null, "Palau Sant Jordi", barcelona);

        List<Booking> conflicts = TerritorialExclusivityService.findConflicts(
                candidateVenue, existingEventDate.plus(2, ChronoUnit.DAYS), null, List.of(existingBooking));

        assertTrue(conflicts.isEmpty());
    }
}
