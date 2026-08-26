package com.gresk.modules.discovery.domain.model;

import com.gresk.modules.discovery.domain.port.out.DiscoveryProfileSignals;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DiscoveryScoreFormulasTest {

    private static DiscoveryProfileSignals signals(Integer popularity, long reviewCount, long demandCount,
            long verifiedAttendees, long knownBy, long recentDemand, long previousDemand, boolean hasUpcoming) {
        return new DiscoveryProfileSignals(popularity, reviewCount, demandCount, verifiedAttendees, knownBy,
                recentDemand, previousDemand, hasUpcoming, null, null, Instant.now(), null, null, null, null);
    }

    @Test
    void communityScore_combinaReviewsDemandaYAsistentesConTope10() {
        DiscoveryProfileSignals s = signals(50, 10, 10, 10, 0, 0, 0, false);
        // 10*0.5 + 10*0.3 + 10*0.8 = 16 -> capado a 10
        assertEquals(0, new BigDecimal("10").compareTo(DiscoveryScoreFormulas.communityScore(s)));
    }

    @Test
    void smallnessScore_esInversamenteProporcionalALaPopularidad() {
        DiscoveryProfileSignals s = signals(20, 0, 0, 0, 0, 0, 0, false);
        assertEquals(0, new BigDecimal("8.0000000000").compareTo(DiscoveryScoreFormulas.smallnessScore(s)));
    }

    @Test
    void smallnessScore_sinSpotifySeTrataComoElCasoMasNicho() {
        DiscoveryProfileSignals s = signals(null, 0, 0, 0, 0, 0, 0, false);
        assertEquals(0, BigDecimal.TEN.compareTo(DiscoveryScoreFormulas.smallnessScore(s)));
    }

    @Test
    void liveScore_premiaTenerConciertosProximos() {
        assertEquals(0, new BigDecimal("8.0").compareTo(
                DiscoveryScoreFormulas.liveScore(signals(50, 0, 0, 0, 0, 0, 0, true))));
        assertEquals(0, new BigDecimal("3.0").compareTo(
                DiscoveryScoreFormulas.liveScore(signals(50, 0, 0, 0, 0, 0, 0, false))));
    }

    @Test
    void momentumScore_creceCuandoLaDemandaRecienteSuperaALaPrevia() {
        DiscoveryProfileSignals s = signals(50, 0, 0, 0, 0, 20, 10, false);
        // growth = (20-10)/10 = 1.0 -> *10 = 10
        assertEquals(0, BigDecimal.TEN.compareTo(DiscoveryScoreFormulas.momentumScore(s)));
    }

    @Test
    void momentumScore_devuelve8SinHistorialPrevioPeroConSenalesRecientes() {
        DiscoveryProfileSignals s = signals(50, 0, 0, 0, 0, 3, 0, false);
        assertEquals(0, new BigDecimal("8.0").compareTo(DiscoveryScoreFormulas.momentumScore(s)));
    }

    @Test
    void greskScore_seMantieneEntre0Y10() {
        DiscoveryProfileSignals maxSignals = signals(0, 100, 100, 100, 0, 100, 1, true);
        DiscoveryProfileSignals minSignals = signals(100, 0, 0, 0, 0, 0, 100, false);

        BigDecimal max = DiscoveryScoreFormulas.greskScore(maxSignals);
        BigDecimal min = DiscoveryScoreFormulas.greskScore(minSignals);

        assertTrue(max.compareTo(BigDecimal.ZERO) >= 0 && max.compareTo(BigDecimal.TEN) <= 0);
        assertTrue(min.compareTo(BigDecimal.ZERO) >= 0 && min.compareTo(BigDecimal.TEN) <= 0);
        assertTrue(max.compareTo(min) > 0);
    }
}
