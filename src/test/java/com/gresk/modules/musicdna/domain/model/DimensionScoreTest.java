package com.gresk.modules.musicdna.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DimensionScoreTest {

    @Test
    void rechazaScoreFueraDeRango() {
        assertThrows(IllegalArgumentException.class,
                () -> new DimensionScore(BigDecimal.ZERO, new BigDecimal("10.01"), "x"));
        assertThrows(IllegalArgumentException.class,
                () -> new DimensionScore(BigDecimal.ZERO, new BigDecimal("-0.01"), "x"));
    }

    @Test
    void fromRatio_saturaEnDiezAlAlcanzarElCap() {
        DimensionScore score = DimensionScore.fromRatio(MusicDnaDimension.INTENSIDAD, new BigDecimal("3.0"));

        assertEquals(new BigDecimal("10.00"), score.score());
        assertEquals("Obsesivo", score.label());
    }

    @Test
    void fromRatio_saturaEnDiezPorEncimaDelCap() {
        DimensionScore score = DimensionScore.fromRatio(MusicDnaDimension.INTENSIDAD, new BigDecimal("100"));

        assertEquals(new BigDecimal("10.00"), score.score());
    }

    @Test
    void fromRatio_normalizaProporcionalmenteAlCap() {
        DimensionScore score = DimensionScore.fromRatio(MusicDnaDimension.LOCALISMO, new BigDecimal("0.5"));

        assertEquals(new BigDecimal("5.00"), score.score());
        assertEquals("Local", score.label());
    }

    @Test
    void zero_devuelveScoreYRawEnCero() {
        DimensionScore score = DimensionScore.zero();

        assertEquals(BigDecimal.ZERO, score.score());
        assertEquals(BigDecimal.ZERO, score.rawValue());
    }
}
