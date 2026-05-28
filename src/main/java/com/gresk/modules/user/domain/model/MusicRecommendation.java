package com.gresk.modules.user.domain.model;

import com.gresk.shared.domain.MusicGenre;
import com.gresk.shared.domain.recommendation.RecommendationLabel;

import java.util.Objects;

public record MusicRecommendation(
        String trackName,
        String artistName,
        String spotifyUrl,
        String imageUrl,
        MusicGenre genre,
        RecommendationLabel label,
        boolean isGresKArtist
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

    public MusicRecommendation(String trackName, String artistName, String spotifyUrl,
                               String imageUrl, MusicGenre genre) {
        this(trackName, artistName, spotifyUrl, imageUrl, genre, null, false);
    }

    public MusicRecommendation withLabel(RecommendationLabel newLabel) {
        return new MusicRecommendation(trackName, artistName, spotifyUrl, imageUrl,
                genre, newLabel, isGresKArtist);
    }

    public MusicRecommendation withIsGresKArtist(boolean flag) {
        return new MusicRecommendation(trackName, artistName, spotifyUrl, imageUrl,
                genre, label, flag);
    }

    public String displayTitle() {
        return String.format("%s - %s [%s]", artistName, trackName, genre.name());
    }
}