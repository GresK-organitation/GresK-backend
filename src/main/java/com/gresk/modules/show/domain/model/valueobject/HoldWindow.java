package com.gresk.modules.show.domain.model.valueobject;

import java.time.Instant;

/** Ventana de la opción/hold sobre el recinto mientras el show está en {@code OPCION_HOLD}. */
public record HoldWindow(Instant placedAt, Instant expiresAt) {

    public HoldWindow {
        if (placedAt == null || expiresAt == null || !expiresAt.isAfter(placedAt)) {
            throw new IllegalArgumentException("HoldWindow expiresAt must be after placedAt");
        }
    }

    public static HoldWindow startingNow(Instant expiresAt) {
        return new HoldWindow(Instant.now(), expiresAt);
    }

    public boolean isExpired(Instant now) {
        return now.isAfter(expiresAt);
    }
}
