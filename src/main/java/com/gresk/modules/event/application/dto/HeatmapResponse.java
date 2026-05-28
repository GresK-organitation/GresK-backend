package com.gresk.modules.event.application.dto;

import java.util.List;

public record HeatmapResponse(
        int year,
        int month,
        List<HeatmapDayResponse> days
) {}
