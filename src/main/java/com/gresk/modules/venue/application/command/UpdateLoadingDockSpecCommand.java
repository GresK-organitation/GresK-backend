package com.gresk.modules.venue.application.command;

import java.time.LocalTime;

public record UpdateLoadingDockSpecCommand(
        String venueId,
        String promoterId,
        double accessHeightMeters,
        double accessWidthMeters,
        double maxVehicleWeightKg,
        LocalTime windowStart,
        LocalTime windowEnd,
        int dockCount,
        String notes
) {
}
