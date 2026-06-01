package com.gresk.modules.rider.application.usecase;

import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.rider.domain.exception.ChecklistNotFoundException;
import com.gresk.modules.rider.domain.model.EventRiderChecklist;
import com.gresk.modules.rider.domain.port.out.ChecklistRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetEventChecklistUseCase {

    private final ChecklistRepositoryPort checklistRepository;

    @Transactional(readOnly = true)
    public EventRiderChecklist execute(String eventId) {
        return checklistRepository.findByEventId(EventId.of(eventId))
                .orElseThrow(() -> new ChecklistNotFoundException(eventId));
    }
}
