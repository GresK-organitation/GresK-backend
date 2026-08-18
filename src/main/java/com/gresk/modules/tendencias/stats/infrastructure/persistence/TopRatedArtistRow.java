package com.gresk.modules.tendencias.stats.infrastructure.persistence;

import java.util.UUID;

public interface TopRatedArtistRow {
    UUID getArtistId();
    String getArtistName();
    Double getAvgRating();
    Long getReviewCount();
}
