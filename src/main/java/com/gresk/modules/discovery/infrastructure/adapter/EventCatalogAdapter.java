package com.gresk.modules.discovery.infrastructure.adapter;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.discovery.domain.port.out.EventCatalogPort;
import com.gresk.modules.discovery.domain.port.out.UpcomingEventInfo;
import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.event.domain.port.out.EventFilter;
import com.gresk.modules.event.domain.port.out.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** Lookup de solo lectura al módulo `event` para la actividad en vivo de un artista. */
@Component
@RequiredArgsConstructor
public class EventCatalogAdapter implements EventCatalogPort {

    private final EventRepository eventRepository;

    @Override
    public Optional<UpcomingEventInfo> findNextUpcomingEvent(ArtistId artistId) {
        EventFilter filter = EventFilter.forArtist(artistId.value(), Instant.now());
        PageRequest pageRequest = PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "eventDate"));
        List<Event> events = eventRepository.findAll(filter, pageRequest);
        return events.stream().findFirst().map(this::toInfo);
    }

    @Override
    public boolean hasEverHadEvents(ArtistId artistId) {
        EventFilter filter = new EventFilter(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of(artistId.value())
        );
        return eventRepository.count(filter) > 0;
    }

    private UpcomingEventInfo toInfo(Event event) {
        String city = event.getLocation() != null && event.getLocation().address() != null
                ? event.getLocation().address().city().value() : null;
        String venue = event.getLocation() != null ? event.getLocation().venue() : null;
        return new UpcomingEventInfo(event.getId().toString(), event.getTitle(), event.getEventDate(), city, venue);
    }
}
