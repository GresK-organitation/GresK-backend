package com.gresk.modules.discovery.domain.port.out;

import java.time.Instant;
import java.util.List;

/** Datos de solo lectura del catálogo del módulo `artist`, necesarios para Discovery. */
public record ArtistCatalogInfo(
        String artistId,
        String name,
        String origin,
        List<String> genres,
        String imageAssetId,
        String bio,
        double avgRating,
        String spotifyArtistId,
        String spotifyUrl,
        String bandcampUrl,
        Instant createdAt
) {
}
