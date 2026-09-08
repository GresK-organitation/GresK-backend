package com.gresk.modules.artist.application.command;

public record GenerateEpkShareLinkCommand(
        String epkAssetId,
        String promoterId,
        Integer versionNumber,
        long expiresInHours,
        Integer maxDownloads,
        String createdByUserId
) {}
