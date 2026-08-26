package com.gresk.modules.discovery.infrastructure.persistence;

public interface ArtistConnectionRow {
    String getArtistId();
    String getName();
    String getImageAssetId();
    Long getCoReviewCount();
}
