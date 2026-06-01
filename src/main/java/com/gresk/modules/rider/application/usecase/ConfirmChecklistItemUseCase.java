package com.gresk.modules.rider.application.usecase;

import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.event.domain.port.out.EventRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.rider.application.command.ConfirmChecklistItemCommand;
import com.gresk.modules.rider.domain.exception.ChecklistNotFoundException;
import com.gresk.modules.rider.domain.model.EventRiderChecklist;
import com.gresk.modules.rider.domain.model.valueobject.ChecklistEntry;
import com.gresk.modules.rider.domain.port.out.ChecklistRepositoryPort;
import com.gresk.modules.event.domain.exception.EventNotFoundException;
import com.gresk.modules.event.domain.exception.ForbiddenOperationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConfirmChecklistItemUseCase {

    private final ChecklistRepositoryPort checklistRepository;
    private final EventRepository         eventRepository;

    @Transactional
    public EventRiderChecklist execute(ConfirmChecklistItemCommand command) {
        EventId eventId      = EventId.of(command.eventId());
        PromoterId promoterId = PromoterId.of(command.promoterId());

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found: " + command.eventId()));
        if (!event.getPromoterId().equals(promoterId)) {
            throw new ForbiddenOperationException("Event does not belong to this promoter");
        }

        EventRiderChecklist checklist = checklistRepository.findByEventId(eventId)
                .orElseThrow(() -> new ChecklistNotFoundException(command.eventId()));

        UUID entryId = UUID.fromString(command.entryId());
        ChecklistEntry entry = checklist.getItems().stream()
                .filter(e -> e.entryId().equals(entryId))
                .findFirst()
                .orElseThrow(() -> new com.gresk.modules.rider.domain.exception.ChecklistEntryNotFoundException(command.entryId()));

        if (entry.confirmed()) {
            checklist.unconfirmEntry(entryId);
        } else {
            checklist.confirmEntry(entryId, command.notes());
        }

        return checklistRepository.save(checklist);
    }
}
