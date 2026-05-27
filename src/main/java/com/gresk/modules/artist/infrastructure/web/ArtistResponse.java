package com.gresk.modules.artist.infrastructure.web;

import java.util.List;

public record ArtistResponse(
        String       id,
        String       promoterId,
        String       name,
        String       origin,
        List<String> genres,
        String       imageUrl,
        String       bio,
        String       status,
        String       fee,
        String       followers,
        List<String> tags,
        String       contact,
        String       instagramUrl,
        String       spotifyUrl,
        // Vinculación Spotify
        String       spotifyArtistId,
        String       spotifyName,
        String       spotifyImageUrl,
        List<String> spotifyGenres,
        boolean      spotifyLinked,
        int          eventsPlayed,
        double       avgRating,
        String       createdAt
) {}
