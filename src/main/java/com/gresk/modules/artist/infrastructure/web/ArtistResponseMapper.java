package com.gresk.modules.artist.infrastructure.web;

import com.gresk.modules.artist.domain.model.Artist;
import org.springframework.stereotype.Component;

@Component
public class ArtistResponseMapper {

    public ArtistResponse toResponse(Artist artist) {
        return new ArtistResponse(
                artist.getId().value().toString(),
                artist.getPromoterId().value().toString(),
                artist.getName().value(),
                artist.getOrigin().value(),
                artist.getGenres().stream().map(Enum::name).toList(),
                artist.getImageUrl().isEmpty()          ? null : artist.getImageUrl().value(),
                artist.getBio().isEmpty()               ? null : artist.getBio().value(),
                artist.getStatus().name(),
                artist.getFee().isEmpty()               ? null : artist.getFee().value(),
                artist.getFollowers().isEmpty()         ? null : artist.getFollowers().value(),
                artist.getTags().stream().toList(),
                artist.getContact().value(),
                artist.getSocialLinks().hasInstagram()  ? artist.getSocialLinks().instagramUrl() : null,
                artist.getSocialLinks().hasSpotify()    ? artist.getSocialLinks().spotifyUrl()   : null,
                artist.getEventsPlayed(),
                artist.getAvgRating(),
                artist.getCreatedAt().toString()
        );
    }
}
