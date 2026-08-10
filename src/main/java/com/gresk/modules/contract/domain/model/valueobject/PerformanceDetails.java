package com.gresk.modules.contract.domain.model.valueobject;

import java.time.LocalDate;

public record PerformanceDetails(
        String    venue,
        LocalDate eventDate,
        Integer   durationMinutes,
        String    showTime
) {}
