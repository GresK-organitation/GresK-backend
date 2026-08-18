package com.gresk.modules.tendencias.stats.application.dto;

import java.util.UUID;

public record TopRatedArtistResponse(UUID artistId, String artistName, double avgRating, long reviewCount) {
}
