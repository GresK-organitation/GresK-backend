package com.gresk.modules.event.application.usecase;

import com.gresk.modules.event.domain.exception.EventNotFoundException;
import com.gresk.modules.event.domain.exception.ForbiddenOperationException;
import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.event.domain.port.out.EventRepository;
import com.gresk.modules.promoter.domain.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PublishEventUseCase {

    private final EventRepository eventRepository;

    public Mono<Event> execute(String eventId, String requesterId) {
        return Mono.defer(() -> {
            EventId id = EventId.of(eventId);
            PromoterId requester = PromoterId.of(requesterId);

            return eventRepository.findById(id)
                    .switchIfEmpty(Mono.error(new EventNotFoundException(eventId)))
                    .flatMap(event -> {
                        if (!event.getPromoterId().equals(requester)) {
                            return Mono.error(new ForbiddenOperationException(
                                    "Promoter " + requesterId + " is not the owner of event " + eventId));
                        }
                        return Mono.fromCallable(() -> {
                            event.publish();
                            return event;
                        });
                    })
                    .flatMap(eventRepository::save);
        });
    }
}
