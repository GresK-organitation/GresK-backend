package com.gresk.modules.user.application.dto;

import com.gresk.modules.user.domain.model.MusicRecommendation;
import com.gresk.shared.domain.MusicGenre;
import lombok.Builder;

@Builder
public record MusicRecommendedDTO(
        String trackName,
        String artistName,
        String spotifyUrl,
        String imageUrl,
        MusicGenre genre
) {

    public static MusicRecommendedDTO fromDomain(MusicRecommendation domain) {
        return MusicRecommendedDTO.builder()
                .trackName(domain.trackName())
                .artistName(domain.artistName())
                .spotifyUrl(domain.spotifyUrl())
                .imageUrl(domain.imageUrl())
                .genre(domain.genre())
                .build();
    }
}
