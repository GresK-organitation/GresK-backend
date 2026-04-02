package com.gresk.modules.user.domain.port.out;

import com.gresk.modules.user.domain.model.MusicRecommendation;
import com.gresk.modules.user.domain.model.UserId;

import java.util.List;

public interface MusicRecommendationProvider {
    List<MusicRecommendation> getSpotifyTopTracks(UserId userId);
}
