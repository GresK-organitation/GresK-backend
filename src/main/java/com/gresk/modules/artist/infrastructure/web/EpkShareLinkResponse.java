package com.gresk.modules.artist.infrastructure.web;

import java.time.Instant;

public record EpkShareLinkResponse(
        String id, String epkAssetId, int versionNumber, String token, String publicUrl,
        Instant expiresAt, Integer maxDownloads, int downloadCount, boolean revoked, Instant createdAt
) {}
