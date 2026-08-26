package com.gresk.modules.discovery.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Proyección nativa de la fila combinada `artists` + `artist_discovery_profile`. */
public interface DiscoveryArtistRow {
    String getArtistId();
    String getName();
    String getOrigin();
    String getGenres();
    String getImageAssetId();
    String getSizeTier();
    Integer getSpotifyPopularity();
    BigDecimal getGreskScore();
    Double getAvgRating();
    Integer getGreskReviewCount();
    Integer getGreskDemandCount();
    Boolean getHasUpcomingEvents();
    LocalDate getNextEventDate();
    String getNextEventCity();
}
