package com.gresk.modules.discovery.infrastructure.web;

public record ArtistConnectionResponse(String artistId, String name, String imageAssetId, long coReviewCount) {}
