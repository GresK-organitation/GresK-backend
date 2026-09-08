package com.gresk.modules.venue.domain.model.valueobject;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

/** Restricción de ruido/hora límite impuesta por la licencia municipal o el vecindario. */
public record CurfewPolicy(LocalTime hardCutoff, Integer maxDecibels, Set<DayOfWeek> restrictedDays, String notes) {

    public CurfewPolicy {
        if (hardCutoff == null) {
            throw new IllegalArgumentException("CurfewPolicy hardCutoff must not be null");
        }
        restrictedDays = restrictedDays == null ? Set.of() : Set.copyOf(restrictedDays);
    }

    public boolean isBreachedBy(LocalTime plannedEnd, DayOfWeek day) {
        boolean dayRestricted = restrictedDays.isEmpty() || restrictedDays.contains(day);
        return dayRestricted && plannedEnd.isAfter(hardCutoff);
    }
}
