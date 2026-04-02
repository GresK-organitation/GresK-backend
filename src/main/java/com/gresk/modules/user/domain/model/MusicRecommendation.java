package com.gresk.modules.user.domain.model;

import com.gresk.shared.domain.MusicGenre;
import java.util.Objects;

public record MusicRecommendation(
        String trackName,
        String artistName,
        String spotifyUrl,
        String imageUrl,
        MusicGenre genre
) {
    public MusicRecommendation {
        Objects.requireNonNull(trackName, "Track name is required");
        Objects.requireNonNull(artistName, "Artist name is required");
        Objects.requireNonNull(spotifyUrl, "Spotify URL is required");

        if (trackName.isBlank()) throw new IllegalArgumentException("Track name cannot be empty");
        if (artistName.isBlank()) throw new IllegalArgumentException("Artist name cannot be empty");

        if (genre == null) {
            genre = MusicGenre.SURPRISE;
        }
    }

    public String displayTitle() {
        return String.format("%s - %s [%s]", artistName, trackName, genre.name());
    }
}