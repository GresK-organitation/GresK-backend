package com.gresk.modules.rider.application.dto;

import java.time.Instant;

public record PendingRiderDto(
        String eventId,
        String eventTitle,
        Instant eventDate,
        String riderId,
        String riderName,
        int unconfirmedRequiredCount,
        int totalRequiredCount,
        int completionPercent
) {}
