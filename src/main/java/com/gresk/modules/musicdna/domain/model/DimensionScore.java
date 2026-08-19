package com.gresk.modules.musicdna.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Valor de una dimensión del ADN Musical: el ratio bruto calculado por
 * {@link MusicDnaFormulas}, el score normalizado en [0,10] y la etiqueta
 * emocional visible al usuario ("Asiduo", "Ecléctico"...).
 */
public record DimensionScore(BigDecimal rawValue, BigDecimal score, String label) {

    public DimensionScore {
        Objects.requireNonNull(rawValue, "rawValue is required");
        Objects.requireNonNull(score, "score is required");
        Objects.requireNonNull(label, "label is required");
        if (score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(BigDecimal.TEN) > 0) {
            throw new IllegalArgumentException("score must be in [0,10], was: " + score);
        }
    }

    static DimensionScore fromRatio(MusicDnaDimension dimension, BigDecimal rawRatio) {
        String label = dimension.thresholds().labelFor(rawRatio);
        BigDecimal score = normalizeToScore(rawRatio, dimension.scoreCap());
        return new DimensionScore(rawRatio, score, label);
    }

    static DimensionScore zero() {
        return new DimensionScore(BigDecimal.ZERO, BigDecimal.ZERO, "Sin datos");
    }

    private static BigDecimal normalizeToScore(BigDecimal rawRatio, BigDecimal capValue) {
        BigDecimal scaled = rawRatio
                .divide(capValue, 10, RoundingMode.HALF_UP)
                .multiply(BigDecimal.TEN);
        BigDecimal bounded = scaled.max(BigDecimal.ZERO).min(BigDecimal.TEN);
        return bounded.setScale(2, RoundingMode.HALF_UP);
    }
}
