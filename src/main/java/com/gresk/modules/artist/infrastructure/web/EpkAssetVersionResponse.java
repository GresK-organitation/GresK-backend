package com.gresk.modules.artist.infrastructure.web;

import java.time.Instant;

public record EpkAssetVersionResponse(
        int versionNumber, String downloadUrl, String fileName, String mimeType,
        long fileSizeBytes, String uploadedByUserId, Instant uploadedAt
) {}
