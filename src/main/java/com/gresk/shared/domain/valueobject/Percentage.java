package com.gresk.shared.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Percentage(int value) {

    public Percentage {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100, got: " + value);
        }
    }

    public static Percentage of(int value) {
        return new Percentage(value);
    }

    /** Returns the fraction as BigDecimal (e.g. 25 → 0.25). */
    public BigDecimal asFraction() {
        return BigDecimal.valueOf(value)
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return value + "%";
    }
}
