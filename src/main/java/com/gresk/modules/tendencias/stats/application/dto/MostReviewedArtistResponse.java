package com.gresk.modules.tendencias.stats.application.dto;

import java.util.UUID;

public record MostReviewedArtistResponse(UUID artistId, String artistName, long reviewCount) {
}
