package com.gresk.modules.musicdna.domain.model;

import java.math.BigDecimal;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Tabla de umbrales (límite inferior inclusivo → etiqueta) reutilizada por
 * las 6 dimensiones del ADN Musical, para no repetir 6 cadenas if/else.
 */
public record DimensionThresholds(NavigableMap<BigDecimal, String> labelsByLowerBoundInclusive) {

    public DimensionThresholds {
        if (labelsByLowerBoundInclusive == null || labelsByLowerBoundInclusive.isEmpty()) {
            throw new IllegalArgumentException("DimensionThresholds requires at least one bound");
        }
    }

    public static DimensionThresholds of(Object... lowerBoundLabelPairs) {
        if (lowerBoundLabelPairs.length == 0 || lowerBoundLabelPairs.length % 2 != 0) {
            throw new IllegalArgumentException("Pairs of (lowerBound, label) are required");
        }
        NavigableMap<BigDecimal, String> map = new TreeMap<>();
        for (int i = 0; i < lowerBoundLabelPairs.length; i += 2) {
            BigDecimal lowerBound = new BigDecimal(lowerBoundLabelPairs[i].toString());
            String label = (String) lowerBoundLabelPairs[i + 1];
            map.put(lowerBound, label);
        }
        return new DimensionThresholds(map);
    }

    public String labelFor(BigDecimal rawRatio) {
        var entry = labelsByLowerBoundInclusive.floorEntry(rawRatio);
        return entry != null ? entry.getValue() : labelsByLowerBoundInclusive.firstEntry().getValue();
    }
}
