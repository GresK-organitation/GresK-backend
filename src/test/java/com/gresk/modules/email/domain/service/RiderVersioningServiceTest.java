package com.gresk.modules.email.domain.service;

import com.gresk.modules.email.domain.model.RiderDiff;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RiderVersioningServiceTest {

    private final RiderVersioningService service = new RiderVersioningService();

    @Test
    void soloAnadidos_detectaLasClavesNuevas() {
        Map<String, Object> previous = Map.of("pa_system", "L-Acoustics");
        Map<String, Object> incoming = Map.of(
                "pa_system", "L-Acoustics",
                "monitores", 4,
                "backline", "batería completa");

        RiderDiff diff = service.computeDiff(previous, incoming);

        assertTrue(diff.hasChanges());
        assertEquals(2, diff.added().size());
        assertTrue(diff.added().contains("monitores: 4"));
        assertTrue(diff.added().contains("backline: batería completa"));
        assertTrue(diff.removed().isEmpty());
        assertTrue(diff.modified().isEmpty());
    }

    @Test
    void soloModificados_detectaElCambioDeValor() {
        Map<String, Object> previous = Map.of("monitores", 4, "pa_system", "L-Acoustics");
        Map<String, Object> incoming = Map.of("monitores", 6, "pa_system", "L-Acoustics");

        RiderDiff diff = service.computeDiff(previous, incoming);

        assertTrue(diff.hasChanges());
        assertTrue(diff.added().isEmpty());
        assertTrue(diff.removed().isEmpty());
        assertEquals(1, diff.modified().size());
        assertEquals("monitores", diff.modified().get(0).field());
        assertEquals("4", diff.modified().get(0).oldValue());
        assertEquals("6", diff.modified().get(0).newValue());
    }

    @Test
    void mixCompleto_detectaAnadidosEliminadosYModificados() {
        Map<String, Object> previous = Map.of(
                "monitores", 4,
                "catering", "vegetariano",
                "pa_system", "L-Acoustics");
        Map<String, Object> incoming = Map.of(
                "monitores", 6,
                "pa_system", "L-Acoustics",
                "backline", "batería completa");

        RiderDiff diff = service.computeDiff(previous, incoming);

        assertTrue(diff.hasChanges());
        assertEquals(1, diff.added().size());
        assertTrue(diff.added().contains("backline: batería completa"));
        assertEquals(1, diff.removed().size());
        assertTrue(diff.removed().contains("catering: vegetariano"));
        assertEquals(1, diff.modified().size());
        assertEquals("monitores", diff.modified().get(0).field());
    }

    @Test
    void sinCambios_devuelveDiffVacio() {
        Map<String, Object> rider = Map.of("monitores", 4);

        RiderDiff diff = service.computeDiff(rider, rider);

        assertFalse(diff.hasChanges());
    }
}
