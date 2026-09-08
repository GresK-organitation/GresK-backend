package com.gresk.modules.booking.domain.model.valueobject;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Offset relativo a la fecha del evento de un booking, en días.
 * Negativo = antes del evento (ej. -30 = "30 días antes"), positivo = después, 0 = mismo día.
 */
public record MilestoneOffset(int daysOffset) {

    public static MilestoneOffset beforeEvent(int days) {
        return new MilestoneOffset(-Math.abs(days));
    }

    public static MilestoneOffset afterEvent(int days) {
        return new MilestoneOffset(Math.abs(days));
    }

    public static MilestoneOffset sameDay() {
        return new MilestoneOffset(0);
    }

    public Instant applyTo(Instant eventDate) {
        return eventDate.plus(daysOffset, ChronoUnit.DAYS);
    }
}
