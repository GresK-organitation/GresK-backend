package com.gresk.modules.review.infrastructure.adapter;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.port.out.ArtistRepositoryPort;
import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.event.domain.port.out.EventRepository;
import com.gresk.modules.review.domain.port.out.ArtistRatingPort;
import com.gresk.modules.review.infrastructure.persistence.ArtistRatingQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Actualiza el avgRating del Artist vinculado al evento tras una review.
 * Sólo usa artist_rating de todas las reviews de los eventos de ese artista.
 */
@Component
@RequiredArgsConstructor
public class ArtistRatingAdapter implements ArtistRatingPort {

    private final EventRepository            eventRepository;
    private final ArtistRepositoryPort       artistRepository;
    private final ArtistRatingQueryRepository artistRatingQueryRepository;

    @Override
    public void recalculateForEvent(EventId eventId) {
        eventRepository.findById(eventId).ifPresent(event -> {
            if (event.getArtistId() == null) return;

            Double avg = artistRatingQueryRepository
                    .getAvgArtistRatingByArtistId(event.getArtistId());
            if (avg == null) avg = 0.0;

            final double avgRating = avg;
            artistRepository.findById(ArtistId.of(event.getArtistId().toString()))
                    .ifPresent(artist -> {
                        artist.updateAvgRating(avgRating);
                        artistRepository.save(artist);
                    });
        });
    }
}
