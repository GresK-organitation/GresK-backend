package com.gresk.modules.booking.domain.model.valueobject;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Cláusula de exclusividad territorial ("radius clause"): protege un territorio alrededor
 * de la fecha del evento durante una ventana de {@code daysBefore}/{@code daysAfter} días.
 */
public record TerritorialExclusivity(Territory protectedTerritory, int daysBefore, int daysAfter) {

    public TerritorialExclusivity {
        if (protectedTerritory == null) {
            throw new IllegalArgumentException("TerritorialExclusivity protectedTerritory must not be null");
        }
        if (daysBefore < 0) {
            throw new IllegalArgumentException("TerritorialExclusivity daysBefore must be >= 0");
        }
        if (daysAfter < 0) {
            throw new IllegalArgumentException("TerritorialExclusivity daysAfter must be >= 0");
        }
    }

    public boolean windowOverlaps(Instant thisEventDate, Instant otherEventDate) {
        Instant windowStart = thisEventDate.minus(daysBefore, ChronoUnit.DAYS);
        Instant windowEnd = thisEventDate.plus(daysAfter, ChronoUnit.DAYS);
        return !otherEventDate.isBefore(windowStart) && !otherEventDate.isAfter(windowEnd);
    }
}
