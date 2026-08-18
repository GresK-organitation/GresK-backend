package com.gresk.modules.tendencias.stats.application.query;

import java.time.Instant;

public record HighestSellThroughQuery(Instant from, Instant to, int limit) {
}
