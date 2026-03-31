package com.gresk.modules.event.application.usecase;

import com.gresk.modules.event.domain.model.*;
import com.gresk.modules.event.domain.port.out.EventRepository;
import com.gresk.modules.promoter.domain.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CreateEventUseCase {

    private final EventRepository eventRepository;

    public Mono<EventId> execute(CreateEventCommand command) {
        return Mono.defer(() -> {
            PromoterId promoterId = PromoterId.of(command.promoterId());

            return Mono.fromCallable(() -> {
                        Event event = Event.create(command.title(), promoterId);

                        if (command.genre() != null) {
                            event.withGenre(Genre.valueOf(command.genre()));
                        }
                        if (command.amount() != null) {
                            event.withPrice(new Price(command.amount(), command.currency()));
                        }
                        if (command.totalCapacity() != null) {
                            event.withCapacity(Capacity.of(command.totalCapacity()));
                        }
                        if (command.eventDate() != null) {
                            event.withEventDate(command.eventDate());
                        }
                        if (command.city() != null) {
                            event.withLocation(new Location(command.city(), command.address(), command.venue()));
                        }
                        if (command.revealAt() != null) {
                            event.withRevealAt(command.revealAt());
                        }

                        return event;
                    })
                    .flatMap(eventRepository::save)
                    .map(Event::getId);
        });
    }
}
