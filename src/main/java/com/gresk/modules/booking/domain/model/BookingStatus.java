package com.gresk.modules.booking.domain.model;

import java.util.EnumSet;
import java.util.Set;

/**
 * Máquina de estados del ciclo de vida de una reserva (hold) de artista+venue+fecha.
 * Inspirada en el sistema "First Hold / Second Hold" de la industria de booking musical.
 * Las transiciones válidas se declaran explícitamente aquí (no hay motor BPMN externo).
 */
public enum BookingStatus {
    HOLD_1,
    HOLD_2,
    CONFIRMED,
    CANCELLED,
    EXPIRED;

    private static final Set<BookingStatus> HOLD_1_TARGETS = EnumSet.of(HOLD_2, CONFIRMED, CANCELLED, EXPIRED);
    private static final Set<BookingStatus> HOLD_2_TARGETS = EnumSet.of(CONFIRMED, CANCELLED, EXPIRED);
    private static final Set<BookingStatus> CONFIRMED_TARGETS = EnumSet.of(CANCELLED);

    public boolean canTransitionTo(BookingStatus target) {
        return switch (this) {
            case HOLD_1 -> HOLD_1_TARGETS.contains(target);
            case HOLD_2 -> HOLD_2_TARGETS.contains(target);
            case CONFIRMED -> CONFIRMED_TARGETS.contains(target);
            case CANCELLED, EXPIRED -> false;
        };
    }

    public boolean isActive() {
        return this == HOLD_1 || this == HOLD_2 || this == CONFIRMED;
    }

    public boolean isHold() {
        return this == HOLD_1 || this == HOLD_2;
    }

    public boolean isTerminal() {
        return this == CANCELLED || this == EXPIRED;
    }
}
