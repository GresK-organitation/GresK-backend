package com.gresk.modules.event.infrastructure.persistence;

import com.gresk.modules.event.domain.model.*;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Address;
import com.gresk.shared.domain.valueobject.AssetId;
import com.gresk.shared.domain.valueobject.City;
import com.gresk.shared.domain.valueobject.Coordinates;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class EventMapper {

    public Event toDomain(EventEntity e) {
        Price price = e.getAmount() != null
                ? new Price(e.getAmount(), e.getCurrency()) : null;

        Price discountedPrice = e.getDiscountedAmount() != null && e.getCurrency() != null
                ? new Price(e.getDiscountedAmount(), e.getCurrency()) : null;

        Capacity capacity = e.getTotalCapacity() != null
                ? new Capacity(e.getTotalCapacity(), e.getAvailableCapacity()) : null;

        Location location = null;
        if (e.getStreet() != null && e.getCity() != null
                && e.getCountry() != null
                && e.getLatitude() != null && e.getLongitude() != null) {
            Address address = new Address(e.getStreet(), City.of(e.getCity()), e.getCountry());
            Coordinates coords = Coordinates.of(e.getLatitude(), e.getLongitude());
            location = new Location(address, coords, e.getVenue());
        }

        AssetId coverImage = (e.getCoverImageAssetId() != null && !e.getCoverImageAssetId().isBlank())
                ? AssetId.reconstitute(e.getCoverImageAssetId()) : null;

        UUID artistId = e.getArtistId();

        EventRatingStats ratingStats = (e.getReviewCount() != null && e.getReviewCount() > 0)
                ? new EventRatingStats(
                        e.getReviewCount(),
                        e.getAvgOverallRating()  != null ? e.getAvgOverallRating()  : 0.0,
                        e.getAvgArtistRating()   != null ? e.getAvgArtistRating()   : 0.0,
                        e.getAvgSoundRating()    != null ? e.getAvgSoundRating()    : 0.0,
                        e.getAvgAmbienceRating() != null ? e.getAvgAmbienceRating() : 0.0,
                        e.getAvgVenueRating()    != null ? e.getAvgVenueRating()    : 0.0,
                        e.getAvgSetlistRating()  != null ? e.getAvgSetlistRating()  : 0.0)
                : EventRatingStats.empty();

        return Event.reconstitute(
                EventId.of(e.getId().toString()),
                e.getTitle(),
                PromoterId.of(e.getPromoterId().toString()),
                e.getGenre(),
                price,
                discountedPrice,
                capacity,
                e.getEventDate(),
                location,
                e.getRevealAt(),
                coverImage,
                artistId,
                e.getStatus(),
                e.getCreatedAt(),
                ratingStats,
                e.isFlashDealEnabled(),
                e.getFlashDealHoursThreshold(),
                e.getFlashDealDiscountPercent(),
                e.isFlashDealApplied()
        );
    }

    public EventEntity toEntity(Event event) {
        Location loc = event.getLocation();

        return EventEntity.builder()
                .id(event.getId().value())
                .title(event.getTitle())
                .promoterId(event.getPromoterId().value())
                .status(event.getStatus())
                .genre(event.getGenre())
                // precio
                .amount(event.getPrice() != null ? event.getPrice().amount()   : null)
                .currency(event.getPrice() != null ? event.getPrice().currency() : null)
                .discountedAmount(event.getDiscountedPrice() != null
                        ? event.getDiscountedPrice().amount() : null)
                // aforo
                .totalCapacity(event.getCapacity() != null ? event.getCapacity().total()     : null)
                .availableCapacity(event.getCapacity() != null ? event.getCapacity().available() : null)
                // fechas
                .eventDate(event.getEventDate())
                .revealAt(event.getRevealAt())
                .createdAt(event.getCreatedAt() != null ? event.getCreatedAt() : Instant.now())
                .updatedAt(Instant.now())
                // ubicación
                .street(loc != null ? loc.address().street()        : null)
                .city(loc != null ? loc.address().city().value()  : null)
                .country(loc != null ? loc.address().country()      : null)
                .venue(loc != null ? loc.venue()                   : null)
                .latitude(loc != null ? loc.coordinates().latitude()  : null)
                .longitude(loc != null ? loc.coordinates().longitude() : null)
                // imagen (public_id de Cloudinary)
                .coverImageAssetId(event.getCoverImage() != null && !event.getCoverImage().isEmpty()
                        ? event.getCoverImage().value() : null)
                // artista (solo FK)
                .artistId(event.getArtistId())
                // flash deal
                .flashDealEnabled(event.isFlashDealEnabled())
                .flashDealHoursThreshold(event.getFlashDealHoursThreshold())
                .flashDealDiscountPercent(event.getFlashDealDiscountPercent())
                .flashDealApplied(event.isFlashDealApplied())
                // rating stats
                .reviewCount(event.getRatingStats().reviewCount())
                .avgOverallRating(event.getRatingStats().avgOverallRating())
                .avgArtistRating(event.getRatingStats().avgArtistRating())
                .avgSoundRating(event.getRatingStats().avgSoundRating())
                .avgAmbienceRating(event.getRatingStats().avgAmbienceRating())
                .avgVenueRating(event.getRatingStats().avgVenueRating())
                .avgSetlistRating(event.getRatingStats().avgSetlistRating())
                .build();
    }
}
