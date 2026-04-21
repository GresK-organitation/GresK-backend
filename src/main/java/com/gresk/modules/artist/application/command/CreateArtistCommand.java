package com.gresk.modules.artist.application.command;

import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public record CreateArtistCommand(
        String      promoterId,
        String      name,
        String      origin,
        Set<String> genres,
        MultipartFile imageAssetId,
        String      bio,
        String      status,
        String      fee,
        String      followers,
        Set<String> tags,
        String      contact,
        String      instagramUrl,
        String      spotifyUrl
) {}
