package com.gresk.modules.rider.infrastructure.web;

import java.time.Instant;
import java.util.List;

public record ChecklistResponse(
        String id,
        String eventId,
        String riderId,
        int completionPercent,
        List<ChecklistEntryResponse> items,
        Instant alertSentAt,
        Instant createdAt,
        Instant updatedAt
) {}
