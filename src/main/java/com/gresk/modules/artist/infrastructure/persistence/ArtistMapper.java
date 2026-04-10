package com.gresk.modules.artist.infrastructure.persistence;

import com.gresk.modules.artist.domain.model.Artist;
import com.gresk.modules.artist.domain.model.ArtistId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import org.springframework.stereotype.Component;

@Component
public class ArtistMapper {

    public Artist toDomain(ArtistEntity entity) {
        return Artist.reconstitute(
                new ArtistId(entity.getId()),
                new PromoterId(entity.getPromoterId()),
                entity.getName(),
                entity.getOrigin(),
                entity.getGenres(),
                entity.getImageUrl(),
                entity.getBio(),
                entity.getStatus(),
                entity.getFee(),
                entity.getFollowers(),
                entity.getContact(),
                entity.getSocialSpotify(),
                entity.getSocialInstagram(),
                entity.getTags(),
                entity.getEventsPlayed(),
                entity.getCreatedAt()
        );
    }

    public ArtistEntity toEntity(Artist artist) {
        return ArtistEntity.builder()
                .id(artist.getId().value())
                .promoterId(artist.getPromoterId().value())
                .name(artist.getName())
                .origin(artist.getOrigin())
                .imageUrl(artist.getImageUrl())
                .bio(artist.getBio())
                .status(artist.getStatus())
                .fee(artist.getFee())
                .followers(artist.getFollowers())
                .contact(artist.getContact())
                .socialSpotify(artist.getSocialSpotify())
                .socialInstagram(artist.getSocialInstagram())
                .genres(new java.util.ArrayList<>(artist.getGenres()))
                .tags(new java.util.ArrayList<>(artist.getTags()))
                .eventsPlayed(artist.getEventsPlayed())
                .build();
    }
}
