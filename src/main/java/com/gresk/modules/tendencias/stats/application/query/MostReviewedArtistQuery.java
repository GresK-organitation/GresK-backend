package com.gresk.modules.tendencias.stats.application.query;

import java.time.Instant;

public record MostReviewedArtistQuery(Instant from, Instant to, int limit) {
}
