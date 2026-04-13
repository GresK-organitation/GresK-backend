package com.gresk.modules.event.application.dto;

import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.event.domain.model.Location;
import org.springframework.stereotype.Component;

@Component
public class EventResponseMapper {

    public EventResponse toResponse(Event event) {
        Location loc = event.getLocation();

        return new EventResponse(
                event.getId().toString(),
                event.getTitle(),
                event.getPromoterId().toString(),
                event.getStatus().name(),
                // genre
                event.getGenre() != null ? event.getGenre().name() : null,
                // precio original
                event.getPrice() != null ? event.getPrice().amount()   : null,
                // precio con descuento
                event.getDiscountedPrice() != null ? event.getDiscountedPrice().amount() : null,
                // currency
                event.getPrice() != null ? event.getPrice().currency() : null,
                // aforo
                event.getCapacity() != null ? event.getCapacity().total()     : null,
                event.getCapacity() != null ? event.getCapacity().available() : null,
                // fechas
                event.getEventDate(),
                event.getRevealAt(),
                event.getCreatedAt(),
                // ubicación
                loc != null ? loc.address().street()        : null,
                loc != null ? loc.address().city().value()  : null,
                loc != null ? loc.address().country()       : null,
                loc != null ? loc.venue()                   : null,
                loc != null ? loc.coordinates().latitude()  : null,
                loc != null ? loc.coordinates().longitude() : null,
                // imagen de portada
                event.getCoverImage() != null && !event.getCoverImage().isEmpty()
                        ? event.getCoverImage().value() : null,
                // artista
                event.getArtist() != null ? event.getArtist().name() : null,
                event.getArtist() != null && event.getArtist().imageUrl() != null
                        ? event.getArtist().imageUrl().value() : null
        );
    }
}
