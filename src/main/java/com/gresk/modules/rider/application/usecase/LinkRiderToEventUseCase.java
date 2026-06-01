package com.gresk.modules.rider.application.usecase;

import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.event.domain.port.out.EventRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.rider.application.command.LinkRiderToEventCommand;
import com.gresk.modules.rider.domain.exception.*;
import com.gresk.modules.rider.domain.model.EventRiderChecklist;
import com.gresk.modules.rider.domain.model.RiderId;
import com.gresk.modules.rider.domain.model.RiderStatus;
import com.gresk.modules.rider.domain.model.TechnicalRider;
import com.gresk.modules.rider.domain.port.out.ChecklistRepositoryPort;
import com.gresk.modules.rider.domain.port.out.RiderRepositoryPort;
import com.gresk.modules.event.domain.exception.EventNotFoundException;
import com.gresk.modules.event.domain.exception.ForbiddenOperationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LinkRiderToEventUseCase {

    private final RiderRepositoryPort    riderRepository;
    private final ChecklistRepositoryPort checklistRepository;
    private final EventRepository        eventRepository;

    @Transactional
    public EventRiderChecklist execute(LinkRiderToEventCommand command) {
        RiderId riderId  = RiderId.of(command.riderId());
        EventId eventId  = EventId.of(command.eventId());
        PromoterId promoterId = PromoterId.of(command.promoterId());

        TechnicalRider rider = riderRepository.findById(riderId)
                .orElseThrow(() -> new RiderNotFoundException(command.riderId()));

        if (rider.getStatus() != RiderStatus.PUBLISHED) {
            throw new RiderIncompletException("Rider must be PUBLISHED before linking to an event");
        }
        if (!rider.getPromoterId().equals(promoterId)) {
            throw new RiderNotOwnedException(command.riderId());
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found: " + command.eventId()));
        if (!event.getPromoterId().equals(promoterId)) {
            throw new ForbiddenOperationException("Event does not belong to this promoter");
        }

        if (checklistRepository.findByEventId(eventId).isPresent()) {
            throw new ChecklistAlreadyExistsException(command.eventId());
        }

        EventRiderChecklist checklist = EventRiderChecklist.create(eventId, rider);
        return checklistRepository.save(checklist);
    }
}
