package com.gresk.modules.rider.infrastructure.web;

import java.time.Instant;

public record PendingRiderResponse(
        String eventId,
        String eventTitle,
        Instant eventDate,
        String riderId,
        String riderName,
        int unconfirmedRequiredCount,
        int totalRequiredCount,
        int completionPercent
) {}
