package com.gresk.modules.event.application.usecase;

import com.gresk.modules.artist.domain.model.Artist;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.port.out.ArtistRepositoryPort;
import com.gresk.modules.event.domain.exception.EventNotFoundException;
import com.gresk.modules.event.domain.exception.ForbiddenOperationException;
import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.event.domain.port.out.EventRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PublishEventUseCase {

    private final EventRepository      eventRepository;
    private final ArtistRepositoryPort artistRepository;

    @Transactional
    public Event execute(String eventId, String requesterId) {
        EventId id = EventId.of(eventId);
        PromoterId requester = PromoterId.of(requesterId);

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        if (!event.getPromoterId().equals(requester)) {
            throw new ForbiddenOperationException(
                    "Promoter " + requesterId + " is not the owner of event " + eventId);
        }

        event.publish();
        Event saved = eventRepository.save(event);

        // Incrementar eventsPlayed en el Artist vinculado
        if (saved.getArtistId() != null) {
            artistRepository.findById(ArtistId.of(saved.getArtistId().toString()))
                    .ifPresent(artist -> {
                        artist.incrementEventsPlayed();
                        artistRepository.save(artist);
                    });
        }

        return saved;
    }
}
