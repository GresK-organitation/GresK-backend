package com.gresk.modules.discovery.domain.port.out;

import java.time.Instant;

public record UpcomingEventInfo(
        String eventId,
        String title,
        Instant eventDate,
        String city,
        String venue
) {
}
