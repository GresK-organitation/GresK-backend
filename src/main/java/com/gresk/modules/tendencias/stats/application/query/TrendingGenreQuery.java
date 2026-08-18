package com.gresk.modules.tendencias.stats.application.query;

import java.time.Instant;

public record TrendingGenreQuery(Instant currentFrom, Instant currentTo,
                                  Instant previousFrom, Instant previousTo, int limit) {
}
