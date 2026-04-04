package com.gresk.modules.user.domain.port.out;

import com.gresk.modules.user.domain.model.MusicRecommendation;
import com.gresk.modules.user.domain.model.UserId;
import com.gresk.shared.domain.MusicGenre;

import java.util.List;
import java.util.Set;

public interface MusicRecommendationProvider {
    List<MusicRecommendation> getSpotifyTopTracks(Set<MusicGenre> musicGenreSet);
}
