package com.gresk.modules.rider.application.usecase;

import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.event.domain.port.out.EventRepository;
import com.gresk.modules.rider.domain.model.EventRiderChecklist;
import com.gresk.modules.rider.domain.model.RiderAlert;
import com.gresk.modules.rider.domain.port.out.ChecklistRepositoryPort;
import com.gresk.modules.rider.domain.port.out.RiderAlertRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendRiderAlertUseCase {

    private final ChecklistRepositoryPort checklistRepository;
    private final RiderAlertRepositoryPort alertRepository;
    private final EventRepository         eventRepository;

    @Transactional
    public void execute(Instant from, Instant to) {
        List<EventRiderChecklist> candidates = checklistRepository.findChecklistsNeedingAlert(from, to);
        log.info("Rider alert check: {} checklists with unconfirmed items in window [{}, {}]",
                candidates.size(), from, to);

        for (EventRiderChecklist checklist : candidates) {
            if (checklist.isFullyConfirmed()) continue;

            Optional<Event> eventOpt = eventRepository.findById(checklist.getEventId());
            if (eventOpt.isEmpty()) continue;

            Event event = eventOpt.get();
            long pending = checklist.getItems().stream()
                    .filter(e -> e.required() && !e.confirmed())
                    .count();

            String message = String.format(
                    "El evento \"%s\" tiene %d ítem(s) del rider sin confirmar en menos de 72h.",
                    event.getTitle(), pending);

            RiderAlert alert = RiderAlert.create(
                    event.getPromoterId(),
                    checklist.getEventId(),
                    checklist.getRiderId(),
                    message);
            alertRepository.save(alert);

            checklist.markAlertSent(Instant.now());
            checklistRepository.save(checklist);

            log.info("Rider alert created for event {} — {} pending items", checklist.getEventId(), pending);
        }
    }
}
