package com.gresk.modules.musicdna.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DimensionThresholdsTest {

    @Test
    void devuelveLaEtiquetaDelLimiteInferiorInclusivo() {
        DimensionThresholds thresholds = MusicDnaDimension.INTENSIDAD.thresholds();

        assertEquals("Esporádico", thresholds.labelFor(new BigDecimal("0.0")));
        assertEquals("Esporádico", thresholds.labelFor(new BigDecimal("0.29")));
        assertEquals("Ocasional",  thresholds.labelFor(new BigDecimal("0.3")));
        assertEquals("Habitual",   thresholds.labelFor(new BigDecimal("0.8")));
        assertEquals("Asiduo",     thresholds.labelFor(new BigDecimal("1.5")));
        assertEquals("Obsesivo",   thresholds.labelFor(new BigDecimal("3.0")));
        assertEquals("Obsesivo",   thresholds.labelFor(new BigDecimal("100")));
    }

    @Test
    void porDebajoDelPrimerUmbralUsaLaPrimeraEtiqueta() {
        DimensionThresholds thresholds = MusicDnaDimension.LOCALISMO.thresholds();

        assertEquals("Cosmopolita", thresholds.labelFor(new BigDecimal("-1")));
    }

    @Test
    void rechazaConstruccionSinPares() {
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> DimensionThresholds.of("0.0"));
    }
}
