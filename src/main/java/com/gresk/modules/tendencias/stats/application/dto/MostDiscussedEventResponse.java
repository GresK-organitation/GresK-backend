package com.gresk.modules.tendencias.stats.application.dto;

import java.util.UUID;

public record MostDiscussedEventResponse(UUID eventId, String eventTitle, long reviewCount) {
}
