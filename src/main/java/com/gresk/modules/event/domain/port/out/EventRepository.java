package com.gresk.modules.event.domain.port.out;

import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.event.domain.model.EventId;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventRepository {
    Mono<Event>   save(Event event);
    Mono<Event>   findById(EventId id);
    Flux<Event>   findAll(EventFilter filter, PageRequest pageRequest);
    Mono<Long>    count(EventFilter filter);
    Mono<Boolean> existsById(EventId id);
}
