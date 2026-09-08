package com.gresk.modules.venue.application.command;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

public record UpdateCurfewPolicyCommand(
        String venueId,
        String promoterId,
        LocalTime hardCutoff,
        Integer maxDecibels,
        Set<DayOfWeek> restrictedDays,
        String notes
) {
}
