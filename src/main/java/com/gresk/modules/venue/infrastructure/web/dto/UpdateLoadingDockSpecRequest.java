package com.gresk.modules.venue.infrastructure.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalTime;

public record UpdateLoadingDockSpecRequest(
        @Positive double accessHeightMeters,
        @Positive double accessWidthMeters,
        @Positive double maxVehicleWeightKg,
        @NotNull  LocalTime windowStart,
        @NotNull  LocalTime windowEnd,
        @Min(1)   int dockCount,
        String notes
) {}
