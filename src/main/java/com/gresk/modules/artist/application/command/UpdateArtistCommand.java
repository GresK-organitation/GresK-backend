package com.gresk.modules.artist.application.command;

import java.util.List;

public record UpdateArtistCommand(
        String artistId,
        String promoterId,
        String name,
        String origin,
        List<String> genres,
        String imageUrl,
        String bio,
        String status,
        String fee,
        String followers,
        String contact,
        String socialSpotify,
        String socialInstagram,
        List<String> tags
) {}
