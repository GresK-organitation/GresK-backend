package com.gresk.modules.musicdna.domain.model;

import java.math.BigDecimal;

/**
 * Las 6 dimensiones del ADN Musical, cada una con su tabla de umbrales
 * (ratio bruto → etiqueta) y su {@code scoreCap}: el valor de ratio a
 * partir del cual el score normalizado satura en 10.
 */
public enum MusicDnaDimension {

    INTENSIDAD(
            DimensionThresholds.of(
                    "0.0", "Esporádico",
                    "0.3", "Ocasional",
                    "0.8", "Habitual",
                    "1.5", "Asiduo",
                    "3.0", "Obsesivo"
            ),
            new BigDecimal("3.0")
    ),
    DIVERSIDAD(
            DimensionThresholds.of(
                    "0.0", "Especialista",
                    "0.2", "Selectivo",
                    "0.5", "Ecléctico",
                    "0.8", "Omnívoro"
            ),
            new BigDecimal("0.8")
    ),
    CRITICIDAD(
            DimensionThresholds.of(
                    "0.0", "Silencioso",
                    "0.2", "Observador",
                    "0.5", "Crítico",
                    "0.8", "Cronista"
            ),
            new BigDecimal("0.8")
    ),
    LOCALISMO(
            DimensionThresholds.of(
                    "0.0",  "Cosmopolita",
                    "0.25", "Viajero",
                    "0.5",  "Local",
                    "0.75", "Arraigado"
            ),
            BigDecimal.ONE
    ),
    ANTIGUEDAD(
            DimensionThresholds.of(
                    "0", "Recién llegado",
                    "1", "En construcción",
                    "3", "Veterano",
                    "6", "Archivo vivo"
            ),
            new BigDecimal("8.0")
    ),
    AUTENTICIDAD(
            DimensionThresholds.of(
                    "0.0", "Libre",
                    "0.2", "Honesto",
                    "0.5", "Trazable",
                    "0.8", "Irrefutable"
            ),
            BigDecimal.ONE
    );

    private final DimensionThresholds thresholds;
    private final BigDecimal scoreCap;

    MusicDnaDimension(DimensionThresholds thresholds, BigDecimal scoreCap) {
        this.thresholds = thresholds;
        this.scoreCap = scoreCap;
    }

    public DimensionThresholds thresholds() { return thresholds; }
    public BigDecimal scoreCap() { return scoreCap; }
}
