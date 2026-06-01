package com.gresk.modules.rider.application.usecase;

import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.event.domain.port.out.EventRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.rider.application.dto.PendingRiderDto;
import com.gresk.modules.rider.domain.model.EventRiderChecklist;
import com.gresk.modules.rider.domain.model.TechnicalRider;
import com.gresk.modules.rider.domain.port.out.ChecklistRepositoryPort;
import com.gresk.modules.rider.domain.port.out.RiderRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetPendingRidersUseCase {

    private final ChecklistRepositoryPort checklistRepository;
    private final RiderRepositoryPort     riderRepository;
    private final EventRepository         eventRepository;

    @Transactional(readOnly = true)
    public List<PendingRiderDto> execute(String promoterId) {
        Instant now           = Instant.now();
        Instant thirtyDaysOut = now.plus(30, ChronoUnit.DAYS);

        List<EventRiderChecklist> checklists = checklistRepository
                .findByPromoterIdAndDateRange(PromoterId.of(promoterId), now, thirtyDaysOut);

        List<PendingRiderDto> result = new ArrayList<>();
        for (EventRiderChecklist checklist : checklists) {
            long required  = checklist.getItems().stream().filter(e -> e.required()).count();
            long confirmed = checklist.getItems().stream().filter(e -> e.required() && e.confirmed()).count();
            long pending   = required - confirmed;
            if (pending <= 0) continue;

            Optional<Event> eventOpt = eventRepository.findById(checklist.getEventId());
            Optional<TechnicalRider> riderOpt = riderRepository.findById(checklist.getRiderId());
            if (eventOpt.isEmpty() || riderOpt.isEmpty()) continue;

            Event event = eventOpt.get();
            TechnicalRider rider = riderOpt.get();

            result.add(new PendingRiderDto(
                    checklist.getEventId().toString(),
                    event.getTitle(),
                    event.getEventDate(),
                    rider.getId().toString(),
                    rider.getName(),
                    (int) pending,
                    (int) required,
                    checklist.completionPercent()
            ));
        }

        result.sort((a, b) -> {
            if (a.eventDate() == null) return 1;
            if (b.eventDate() == null) return -1;
            return a.eventDate().compareTo(b.eventDate());
        });

        return result;
    }
}
