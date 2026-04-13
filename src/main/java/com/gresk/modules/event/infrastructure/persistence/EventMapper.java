package com.gresk.modules.event.infrastructure.persistence;

import com.gresk.modules.event.domain.model.*;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Address;
import com.gresk.shared.domain.valueobject.City;
import com.gresk.shared.domain.valueobject.Coordinates;
import com.gresk.shared.domain.valueobject.ImageUrl;
import org.springframework.stereotype.Component;

import java.time.Instant;

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

        ImageUrl coverImage = (e.getCoverImageUrl() != null && !e.getCoverImageUrl().isBlank())
                ? ImageUrl.reconstitute(e.getCoverImageUrl()) : null;

        Artist artist = null;
        if (e.getArtistName() != null && !e.getArtistName().isBlank()) {
            ImageUrl artistImg = (e.getArtistImageUrl() != null && !e.getArtistImageUrl().isBlank())
                    ? ImageUrl.reconstitute(e.getArtistImageUrl()) : null;
            artist = Artist.of(e.getArtistName(), artistImg);
        }

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
                artist,
                e.getStatus(),
                e.getCreatedAt()
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
                // imagen
                .coverImageUrl(event.getCoverImage() != null && !event.getCoverImage().isEmpty()
                        ? event.getCoverImage().value() : null)
                // artista
                .artistName(event.getArtist() != null ? event.getArtist().name() : null)
                .artistImageUrl(event.getArtist() != null && event.getArtist().imageUrl() != null
                        ? event.getArtist().imageUrl().value() : null)
                .build();
    }
}
