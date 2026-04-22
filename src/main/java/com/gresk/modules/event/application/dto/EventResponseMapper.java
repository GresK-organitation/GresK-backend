package com.gresk.modules.event.application.dto;

import com.gresk.modules.artist.domain.model.Artist;
import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.event.domain.model.Location;
import com.gresk.shared.domain.port.out.ImageUrlResolverPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventResponseMapper {

    private final ImageUrlResolverPort imageUrlResolver;

    /**
     * Convierte un Event domain en EventResponse, enriqueciendo los datos
     * del artista desde el agregado Artist (puede ser null si no está vinculado).
     */
    public EventResponse toResponse(Event event, Artist artist) {
        Location loc = event.getLocation();

        String coverImageUrl = imageUrlResolver.resolveOrDefault(event.getCoverImage());

        String artistId       = event.getArtistId() != null ? event.getArtistId().toString() : null;
        String artistName     = artist != null ? artist.getName().value() : null;
        String artistImageUrl = null;
        if (artist != null && artist.getImageAssetId() != null
                && !artist.getImageAssetId().isEmpty()) {
            artistImageUrl = imageUrlResolver.resolveOrDefault(artist.getImageAssetId());
        }

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
                coverImageUrl,
                // artista
                artistId,
                artistName,
                artistImageUrl
        );
    }
}
