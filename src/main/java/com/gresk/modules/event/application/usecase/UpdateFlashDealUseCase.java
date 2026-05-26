package com.gresk.modules.event.application.usecase;

import com.gresk.modules.event.domain.exception.EventNotFoundException;
import com.gresk.modules.event.domain.exception.ForbiddenOperationException;
import com.gresk.modules.event.domain.exception.InvalidEventTransitionException;
import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.event.domain.model.EventStatus;
import com.gresk.modules.event.domain.port.out.EventRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateFlashDealUseCase {

    private final EventRepository eventRepository;

    public Event execute(UpdateFlashDealCommand command) {
        EventId    eventId = EventId.of(command.eventId());
        PromoterId owner   = PromoterId.of(command.promoterId());

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(
                        "Event not found with ID: " + command.eventId()));

        if (!event.getPromoterId().equals(owner)) {
            throw new ForbiddenOperationException(
                    "Promoter is not the owner of this event");
        }

        if (event.getStatus() == EventStatus.FINISHED
                || event.getStatus() == EventStatus.CANCELLED) {
            throw new InvalidEventTransitionException(
                    "Cannot update flash deal on a terminal event (status: " + event.getStatus() + ")");
        }

        event.configureFlashDeal(
                command.flashDealEnabled(),
                command.flashDealHoursThreshold(),
                command.flashDealDiscountPercent()
        );

        // Aplicar inmediatamente si el evento ya está dentro de su ventana temporal.
        // Evita que la promotora tenga que esperar hasta el próximo tick del scheduler.
        if (event.isFlashDealEligible(Instant.now())) {
            event.applyFlashDeal();
        }

        return eventRepository.save(event);
    }
}
