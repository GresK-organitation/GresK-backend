package com.gresk.modules.rider.infrastructure.web;

import java.time.Instant;

public record RiderAlertResponse(
        String id,
        String promoterId,
        String eventId,
        String riderId,
        String message,
        boolean read,
        Instant createdAt
) {}
