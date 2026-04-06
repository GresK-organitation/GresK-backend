package com.gresk.modules.user.infrastructure.in.external;

import com.gresk.modules.user.domain.model.MusicRecommendation;
import com.gresk.modules.user.domain.model.UserId;
import com.gresk.modules.user.domain.port.out.MusicRecommendationProvider;
import com.gresk.shared.domain.MusicGenre;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class SpotifyMusicProviderAdapter implements MusicRecommendationProvider {

    private static final List<MusicRecommendation> MOCK_DATA = List.of(
            new MusicRecommendation(
                    "Starboy",
                    "The Weeknd",
                    "https://open.spotify.com/track/1",
                    "https://img.com/1",
                    MusicGenre.POP
            ),
            new MusicRecommendation(
                    "Blinding Lights",
                    "The Weeknd",
                    "https://open.spotify.com/track/2",
                    "https://img.com/2",
                    MusicGenre.ELECTRONIC
            ),
            new MusicRecommendation(
                    "Levitating",
                    "Dua Lipa",
                    "https://open.spotify.com/track/3",
                    "https://img.com/3",
                    MusicGenre.POP
            )
    );

    @Override
    public Set<MusicRecommendation> getSpotifyTopTracks(Set<MusicGenre> musicGenreSet) {
        return MOCK_DATA.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> {
                            Collections.shuffle(list);
                            return list.stream().limit(2).collect(Collectors.toSet());
                        }
                ));
    }
}