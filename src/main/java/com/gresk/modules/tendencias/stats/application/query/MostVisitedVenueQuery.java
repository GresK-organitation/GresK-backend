package com.gresk.modules.tendencias.stats.application.query;

import java.time.Instant;

public record MostVisitedVenueQuery(Instant from, Instant to, int limit) {
}
