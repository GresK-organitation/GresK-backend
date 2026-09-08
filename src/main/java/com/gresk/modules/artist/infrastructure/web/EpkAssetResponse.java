package com.gresk.modules.artist.infrastructure.web;

import java.time.Instant;
import java.util.List;

public record EpkAssetResponse(
        String id, String artistId, String promoterId, String type, String label,
        boolean archived, List<EpkAssetVersionResponse> versions, Instant createdAt, Instant updatedAt
) {}
