package com.gresk.modules.event.application.dto;

public record HeatmapDayResponse(
        String date,
        int eventCount,
        int avgOccupationPercentage
) {}
