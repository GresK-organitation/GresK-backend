package com.gresk.modules.discovery.domain.port.out;

public record ArtistConnection(
        String artistId,
        String name,
        String imageAssetId,
        long coReviewCount
) {
}
