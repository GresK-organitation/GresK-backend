package com.gresk.modules.artist.application.command;

import org.springframework.web.multipart.MultipartFile;

public record UploadEpkAssetCommand(
        String artistId,
        String promoterId,
        String type,
        String label,
        MultipartFile file,
        String uploadedByUserId
) {}
