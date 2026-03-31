package com.gresk.modules.event.application.usecase;

import com.gresk.modules.event.application.query.GetEventQuery;
import com.gresk.modules.event.domain.exception.EventNotFoundException;
import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.event.domain.port.out.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GetEventUseCase {

    private final EventRepository eventRepository;

    public Mono<Event> execute(GetEventQuery query) {
        return Mono.defer(() -> {
            EventId id = EventId.of(query.eventId());
            return eventRepository.findById(id)
                    .switchIfEmpty(Mono.error(new EventNotFoundException(query.eventId())));
        });
    }
}
