package com.gresk.modules.artist.application.command;

import org.springframework.web.multipart.MultipartFile;

public record AddEpkAssetVersionCommand(
        String epkAssetId,
        String promoterId,
        MultipartFile file,
        String uploadedByUserId
) {}
