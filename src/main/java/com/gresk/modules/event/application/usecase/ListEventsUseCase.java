package com.gresk.modules.event.application.usecase;

import com.gresk.modules.event.application.query.ListEventsQuery;
import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.event.domain.model.EventStatus;
import com.gresk.modules.event.domain.port.out.EventFilter;
import com.gresk.modules.event.domain.port.out.EventRepository;
import com.gresk.shared.domain.MusicGenre;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ListEventsUseCase {

    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    public List<Event> execute(ListEventsQuery query) {
        return eventRepository.findAll(toFilter(query), PageRequest.of(query.page(), query.size()));
    }

    @Transactional(readOnly = true)
    public long count(ListEventsQuery query) {
        return eventRepository.count(toFilter(query));
    }

    private EventFilter toFilter(ListEventsQuery query) {
        return new EventFilter(
                Optional.ofNullable(query.genre()).map(MusicGenre::valueOf),
                Optional.ofNullable(query.city()),
                Optional.ofNullable(query.dateFrom()),
                Optional.ofNullable(query.dateTo()),
                Optional.ofNullable(query.minPrice()),
                Optional.ofNullable(query.maxPrice()),
                Optional.ofNullable(query.artistName()),
                Optional.of(EventStatus.PUBLISHED)
        );
    }
}
