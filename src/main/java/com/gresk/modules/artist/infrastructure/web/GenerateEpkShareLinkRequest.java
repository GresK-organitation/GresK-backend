package com.gresk.modules.artist.infrastructure.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

public record GenerateEpkShareLinkRequest(
        Integer versionNumber,      // null = versión actual
        @Min(1) long expiresInHours,
        @Positive Integer maxDownloads
) {}
