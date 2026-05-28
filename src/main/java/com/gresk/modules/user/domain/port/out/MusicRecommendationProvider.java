package com.gresk.modules.user.domain.port.out;

import com.gresk.modules.user.domain.model.MusicRecommendation;
import com.gresk.shared.domain.MusicGenre;

import java.util.Set;
import java.util.UUID;

public interface MusicRecommendationProvider {
    Set<MusicRecommendation> getSpotifyTopTracks(Set<MusicGenre> musicGenreSet, String city, UUID userId);
}
