package com.gresk.modules.rider.domain.port.out;

import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.rider.domain.model.ChecklistId;
import com.gresk.modules.rider.domain.model.EventRiderChecklist;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ChecklistRepositoryPort {

    EventRiderChecklist save(EventRiderChecklist checklist);

    Optional<EventRiderChecklist> findById(ChecklistId id);

    Optional<EventRiderChecklist> findByEventId(EventId eventId);

    List<EventRiderChecklist> findChecklistsNeedingAlert(Instant from, Instant to);

    List<EventRiderChecklist> findByPromoterIdAndDateRange(PromoterId promoterId, Instant from, Instant to);
}
