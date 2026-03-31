package com.gresk.modules.event.application.usecase;

import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.event.domain.port.out.EventFilter;
import com.gresk.modules.event.domain.port.out.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ListEventsUseCase {

    private final EventRepository eventRepository;

    public Flux<Event> execute(EventFilter filter, PageRequest pageRequest) {
        return eventRepository.findAll(filter, pageRequest);
    }

    public Mono<Long> count(EventFilter filter) {
        return eventRepository.count(filter);
    }
}
