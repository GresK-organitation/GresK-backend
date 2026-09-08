package com.gresk.modules.venue.domain.model.valueobject;

/**
 * Un aforo modular del recinto (p.ej. "PISTA" de pie a 3000, "GRADA" sentada a 1200,
 * "REDUCIDO" con mamparas a 800). {@code code} es el identificador estable que referencia
 * un {@code Show} al elegir configuración; {@code label} es el nombre mostrado.
 */
public record CapacityConfiguration(String code, String label, CapacityLayout layout, int maxCapacity) {

    public CapacityConfiguration {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("CapacityConfiguration code must not be blank");
        }
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("CapacityConfiguration label must not be blank");
        }
        if (layout == null) {
            throw new IllegalArgumentException("CapacityConfiguration layout must not be null");
        }
        if (maxCapacity < 1) {
            throw new IllegalArgumentException("CapacityConfiguration maxCapacity must be at least 1");
        }
        code = code.trim().toUpperCase();
    }
}
