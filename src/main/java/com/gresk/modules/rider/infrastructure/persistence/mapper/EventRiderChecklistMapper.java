package com.gresk.modules.rider.infrastructure.persistence.mapper;

import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.rider.domain.model.ChecklistId;
import com.gresk.modules.rider.domain.model.EventRiderChecklist;
import com.gresk.modules.rider.domain.model.RiderId;
import com.gresk.modules.rider.domain.model.valueobject.ChecklistEntry;
import com.gresk.modules.rider.infrastructure.persistence.entity.ChecklistEntryEntity;
import com.gresk.modules.rider.infrastructure.persistence.entity.EventRiderChecklistEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EventRiderChecklistMapper {

    public EventRiderChecklist toDomain(EventRiderChecklistEntity e) {
        List<ChecklistEntry> items = e.getItems().stream()
                .map(entry -> new ChecklistEntry(
                        entry.getEntryId(),
                        entry.getCategory(),
                        entry.getDescription(),
                        entry.isRequired(),
                        entry.isConfirmed(),
                        entry.getConfirmedAt(),
                        entry.getConfirmedNotes()))
                .toList();

        return EventRiderChecklist.reconstitute(
                ChecklistId.of(e.getId()),
                EventId.of(e.getEventId().toString()),
                RiderId.of(e.getRiderId()),
                items,
                e.getAlertSentAt(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    public EventRiderChecklistEntity toNewEntity(EventRiderChecklist checklist) {
        EventRiderChecklistEntity entity = EventRiderChecklistEntity.builder()
                .id(checklist.getId().value())
                .eventId(checklist.getEventId().value())
                .riderId(checklist.getRiderId().value())
                .alertSentAt(checklist.getAlertSentAt())
                .createdAt(checklist.getCreatedAt())
                .updatedAt(checklist.getUpdatedAt())
                .items(new ArrayList<>())
                .build();

        for (ChecklistEntry entry : checklist.getItems()) {
            entity.getItems().add(toEntryEntity(entry, entity));
        }

        return entity;
    }

    public ChecklistEntryEntity toEntryEntity(ChecklistEntry entry, EventRiderChecklistEntity parent) {
        return ChecklistEntryEntity.builder()
                .entryId(entry.entryId())
                .checklist(parent)
                .category(entry.category())
                .description(entry.description())
                .required(entry.required())
                .confirmed(entry.confirmed())
                .confirmedAt(entry.confirmedAt())
                .confirmedNotes(entry.confirmedNotes())
                .build();
    }
}
