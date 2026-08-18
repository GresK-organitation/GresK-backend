package com.gresk.modules.tendencias.chronicle.application.dto;

import java.time.Instant;
import java.util.UUID;

public record FeedSourceResponse(
        UUID id,
        String name,
        String feedUrl,
        String sourceUrl,
        String status,
        Instant requestedAt,
        Instant reviewedAt,
        Instant lastFetchedAt
) {
}
