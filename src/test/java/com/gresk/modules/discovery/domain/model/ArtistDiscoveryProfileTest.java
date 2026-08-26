package com.gresk.modules.discovery.domain.model;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.discovery.domain.port.out.DiscoveryProfileSignals;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArtistDiscoveryProfileTest {

    @Test
    void calculate_asignaSizeTierYScoreAPartirDeLasSenales() {
        ArtistId artistId = ArtistId.generate();
        Instant createdAt = Instant.now().minusSeconds(3600);
        DiscoveryProfileSignals signals = new DiscoveryProfileSignals(
                10, 5, 3, 2, 8, 2, 1, true, LocalDate.now().plusDays(3), "Barcelona",
                createdAt, null, null, null, null);

        ArtistDiscoveryProfile profile = ArtistDiscoveryProfile.calculate(artistId, signals);

        assertEquals(SizeTier.MICRO, profile.getSizeTier());
        assertEquals(artistId, profile.getArtistId());
        assertEquals(5, profile.getGreskReviewCount());
        assertEquals(3, profile.getGreskDemandCount());
        assertTrue(profile.isHasUpcomingEvents());
        assertEquals(createdAt, profile.getFirstDiscoveredAt());
        assertTrue(profile.getGreskScore().signum() > 0);
    }

    @Test
    void withUpcomingEventFlags_soloActualizaLosCamposDeActividadEnVivo() {
        ArtistId artistId = ArtistId.generate();
        DiscoveryProfileSignals signals = new DiscoveryProfileSignals(
                50, 0, 0, 0, 0, 0, 0, false, null, null, Instant.now(), null, null, null, null);
        ArtistDiscoveryProfile original = ArtistDiscoveryProfile.calculate(artistId, signals);

        ArtistDiscoveryProfile updated = original.withUpcomingEventFlags(true, LocalDate.now().plusDays(5), "Madrid");

        assertTrue(updated.isHasUpcomingEvents());
        assertEquals("Madrid", updated.getNextEventCity());
        assertEquals(original.getGreskScore(), updated.getGreskScore());
        assertFalse(original.isHasUpcomingEvents());
    }
}
