package com.gresk.modules.booking.domain.model;

import com.gresk.modules.booking.domain.exception.HoldExpirationRequiredException;
import com.gresk.modules.booking.domain.exception.InvalidBookingException;
import com.gresk.modules.booking.domain.exception.InvalidBookingStatusTransitionException;
import com.gresk.modules.booking.domain.exception.MilestoneAlreadyCompletedException;
import com.gresk.modules.booking.domain.model.valueobject.MilestoneBlueprint;
import com.gresk.modules.booking.domain.model.valueobject.MilestoneOffset;
import com.gresk.modules.booking.domain.model.valueobject.Territory;
import com.gresk.modules.booking.domain.model.valueobject.VenueRef;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookingTest {

    private final PromoterId promoterId = PromoterId.generate();
    private final UUID artistId = UUID.randomUUID();
    private final VenueRef venue = new VenueRef(null, "Sala X", new Territory("España", "Cataluña", "Barcelona", null));
    private final Instant eventDate = Instant.now().plus(90, ChronoUnit.DAYS);

    @Test
    void creaUnHoldConHitosGeneradosDesdeBlueprints() {
        List<MilestoneBlueprint> blueprints = List.of(
                new MilestoneBlueprint(MilestoneType.PAYMENT, "Depósito inicial", MilestoneOffset.beforeEvent(30)),
                new MilestoneBlueprint(MilestoneType.RIDER_SUBMISSION, "Envío de rider", MilestoneOffset.beforeEvent(14)));

        Booking booking = Booking.create(promoterId, artistId, venue, eventDate, BookingStatus.HOLD_1,
                Instant.now().plus(7, ChronoUnit.DAYS), blueprints, null, null);

        assertEquals(BookingStatus.HOLD_1, booking.getStatus());
        assertEquals(2, booking.getMilestones().size());
    }

    @Test
    void crearHoldSinFechaDeExpiracionFalla() {
        assertThrows(HoldExpirationRequiredException.class, () ->
                Booking.create(promoterId, artistId, venue, eventDate, BookingStatus.HOLD_1, null, List.of(), null, null));
    }

    @Test
    void crearBookingConEstadoInicialNoHoldFalla() {
        assertThrows(InvalidBookingException.class, () ->
                Booking.create(promoterId, artistId, venue, eventDate, BookingStatus.CONFIRMED, null, List.of(), null, null));
    }

    @Test
    void promocionaDeHold1AHold2YLuegoConfirma() {
        Booking booking = Booking.create(promoterId, artistId, venue, eventDate, BookingStatus.HOLD_1,
                Instant.now().plus(7, ChronoUnit.DAYS), List.of(), null, null);

        booking.promoteToHold2(Instant.now().plus(14, ChronoUnit.DAYS));
        assertEquals(BookingStatus.HOLD_2, booking.getStatus());

        booking.confirm();
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
        assertEquals(null, booking.getHoldExpiresAt());
    }

    @Test
    void unBookingConfirmadoNoPuedeVolverAHold() {
        Booking booking = Booking.create(promoterId, artistId, venue, eventDate, BookingStatus.HOLD_1,
                Instant.now().plus(7, ChronoUnit.DAYS), List.of(), null, null);
        booking.confirm();

        assertThrows(InvalidBookingStatusTransitionException.class, () ->
                booking.promoteToHold2(Instant.now().plus(1, ChronoUnit.DAYS)));
    }

    @Test
    void reprogramarRecalculaHitosPendientesYCongelaLosCompletados() {
        List<MilestoneBlueprint> blueprints = List.of(
                new MilestoneBlueprint(MilestoneType.PAYMENT, "Depósito", MilestoneOffset.beforeEvent(30)),
                new MilestoneBlueprint(MilestoneType.LICENSE, "Licencia", MilestoneOffset.beforeEvent(20)));
        Booking booking = Booking.create(promoterId, artistId, venue, eventDate, BookingStatus.HOLD_1,
                Instant.now().plus(7, ChronoUnit.DAYS), blueprints, null, null);

        UUID completedMilestoneId = booking.getMilestones().get(0).milestoneId();
        booking.completeMilestone(completedMilestoneId, "pagado");
        Instant frozenDueDate = booking.getMilestones().get(0).dueDate();

        Instant newEventDate = eventDate.plus(60, ChronoUnit.DAYS);
        booking.reschedule(newEventDate);

        assertEquals(newEventDate, booking.getEventDate());
        assertEquals(frozenDueDate, booking.getMilestones().get(0).dueDate());
        assertTrue(booking.getMilestones().get(1).dueDate().isAfter(frozenDueDate));
    }

    @Test
    void completarUnHitoYaCompletadoFalla() {
        List<MilestoneBlueprint> blueprints = List.of(
                new MilestoneBlueprint(MilestoneType.PAYMENT, "Depósito", MilestoneOffset.beforeEvent(30)));
        Booking booking = Booking.create(promoterId, artistId, venue, eventDate, BookingStatus.HOLD_1,
                Instant.now().plus(7, ChronoUnit.DAYS), blueprints, null, null);
        UUID milestoneId = booking.getMilestones().get(0).milestoneId();
        booking.completeMilestone(milestoneId, "ok");

        assertThrows(MilestoneAlreadyCompletedException.class, () -> booking.completeMilestone(milestoneId, "de nuevo"));
    }

    @Test
    void cancelarUnHoldActivoFunciona() {
        Booking booking = Booking.create(promoterId, artistId, venue, eventDate, BookingStatus.HOLD_1,
                Instant.now().plus(7, ChronoUnit.DAYS), List.of(), null, null);
        booking.cancel("El artista no está disponible");
        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        assertFalse(booking.getStatus().isActive());
    }
}
