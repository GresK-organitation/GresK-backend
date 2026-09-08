package com.gresk.modules.logistics.domain.model;

import java.util.EnumSet;
import java.util.Set;

/**
 * Ciclo de vida de un Tour. Un Tour puede tener una única fecha (leg) o varias
 * consecutivas; el estado no depende del número de legs sino de si la promotora
 * ya lo está ejecutando sobre el terreno.
 */
public enum TourStatus {
    PLANNED,
    ACTIVE,
    COMPLETED,
    CANCELLED;

    private static final Set<TourStatus> PLANNED_TARGETS = EnumSet.of(ACTIVE, CANCELLED);
    private static final Set<TourStatus> ACTIVE_TARGETS = EnumSet.of(COMPLETED, CANCELLED);

    public boolean canTransitionTo(TourStatus target) {
        return switch (this) {
            case PLANNED -> PLANNED_TARGETS.contains(target);
            case ACTIVE -> ACTIVE_TARGETS.contains(target);
            case COMPLETED, CANCELLED -> false;
        };
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED;
    }
}
