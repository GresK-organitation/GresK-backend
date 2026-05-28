package com.gresk.modules.user.domain.port.out;

import com.gresk.shared.domain.MusicGenre;
import com.gresk.shared.domain.recommendation.RecommendationLabel;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface GresKArtistSpotifyPort {
    List<GresKArtistSpotifyData> findByGenres(Set<MusicGenre> genres, String city);

    record GresKArtistSpotifyData(
            UUID artistId,
            String spotifyArtistId,
            String artistName,
            Set<MusicGenre> genres,
            RecommendationLabel suggestedLabel
    ) {}
}
