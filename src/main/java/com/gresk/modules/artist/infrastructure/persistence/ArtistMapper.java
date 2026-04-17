package com.gresk.modules.artist.infrastructure.persistence;

import com.gresk.modules.artist.domain.model.Artist;
import com.gresk.modules.artist.domain.model.valueobject.ArtistContact;
import com.gresk.modules.artist.domain.model.valueobject.ArtistFee;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.model.valueobject.FollowerCount;
import com.gresk.modules.artist.domain.model.valueobject.SocialLinks;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.City;
import com.gresk.shared.domain.valueobject.Description;
import com.gresk.shared.domain.valueobject.ImageUrl;
import com.gresk.shared.domain.valueobject.Name;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashSet;

@Component
public class ArtistMapper {

    public Artist toDomain(ArtistEntity entity) {
        return Artist.reconstitute(
                ArtistId.of(entity.getId().toString()),
                PromoterId.of(entity.getPromoterId().toString()),
                Name.reconstitute(entity.getName()),
                City.of(entity.getOrigin()),
                entity.getGenres(),
                ImageUrl.reconstitute(entity.getImageUrl() != null ? entity.getImageUrl() : ""),
                Description.reconstitute(entity.getBio() != null ? entity.getBio() : ""),
                entity.getStatus(),
                ArtistFee.reconstitute(entity.getFee()),
                FollowerCount.reconstitute(entity.getFollowers()),
                entity.getTags(),
                ArtistContact.reconstitute(entity.getContact()),
                SocialLinks.reconstitute(entity.getInstagramUrl(), entity.getSpotifyUrl()),
                entity.getEventsPlayed(),
                entity.getAvgRating(),
                entity.getCreatedAt()
        );
    }

    public ArtistEntity toEntity(Artist artist) {
        return ArtistEntity.builder()
                .id(artist.getId().value())
                .promoterId(artist.getPromoterId().value())
                .name(artist.getName().value())
                .origin(artist.getOrigin().value())
                .genres(new HashSet<>(artist.getGenres()))
                .imageUrl(artist.getImageUrl().isEmpty() ? null : artist.getImageUrl().value())
                .bio(artist.getBio().isEmpty() ? null : artist.getBio().value())
                .status(artist.getStatus())
                .fee(artist.getFee().isEmpty() ? null : artist.getFee().value())
                .followers(artist.getFollowers().isEmpty() ? null : artist.getFollowers().value())
                .tags(new HashSet<>(artist.getTags()))
                .contact(artist.getContact().value())
                .instagramUrl(artist.getSocialLinks().hasInstagram() ? artist.getSocialLinks().instagramUrl() : null)
                .spotifyUrl(artist.getSocialLinks().hasSpotify()    ? artist.getSocialLinks().spotifyUrl()   : null)
                .eventsPlayed(artist.getEventsPlayed())
                .avgRating(artist.getAvgRating())
                .createdAt(artist.getCreatedAt())
                .updatedAt(Instant.now())
                .build();
    }
}
