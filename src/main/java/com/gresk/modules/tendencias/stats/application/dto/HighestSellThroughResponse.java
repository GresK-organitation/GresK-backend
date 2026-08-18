package com.gresk.modules.tendencias.stats.application.dto;

import java.util.UUID;

public record HighestSellThroughResponse(UUID eventId, String eventTitle, double sellThroughPercent) {
}
