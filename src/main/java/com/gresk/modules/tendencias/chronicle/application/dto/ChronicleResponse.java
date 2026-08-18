package com.gresk.modules.tendencias.chronicle.application.dto;

import java.time.Instant;
import java.util.UUID;

public record ChronicleResponse(
        UUID id,
        String title,
        String excerpt,
        String link,
        String sourceName,
        String sourceUrl,
        Instant originalPublishedAt,
        Instant ingestedAt
) {
}
