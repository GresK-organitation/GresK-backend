package com.gresk.modules.event.domain.model;

import java.time.LocalDate;

public record HeatmapDay(
        LocalDate date,
        int eventCount,
        int avgOccupationPercentage
) {}
