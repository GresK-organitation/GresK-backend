package com.gresk.modules.rider.infrastructure.persistence.adapter;

import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.rider.domain.model.ChecklistId;
import com.gresk.modules.rider.domain.model.EventRiderChecklist;
import com.gresk.modules.rider.domain.model.valueobject.ChecklistEntry;
import com.gresk.modules.rider.domain.port.out.ChecklistRepositoryPort;
import com.gresk.modules.rider.infrastructure.persistence.entity.ChecklistEntryEntity;
import com.gresk.modules.rider.infrastructure.persistence.entity.EventRiderChecklistEntity;
import com.gresk.modules.rider.infrastructure.persistence.mapper.EventRiderChecklistMapper;
import com.gresk.modules.rider.infrastructure.persistence.repository.EventRiderChecklistJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaChecklistRepositoryAdapter implements ChecklistRepositoryPort {

    private final EventRiderChecklistJpaRepository repo;
    private final EventRiderChecklistMapper        mapper;

    @Override
    @Transactional
    public EventRiderChecklist save(EventRiderChecklist checklist) {
        return repo.findById(checklist.getId().value())
                .map(entity -> {
                    entity.setAlertSentAt(checklist.getAlertSentAt());
                    entity.setUpdatedAt(Instant.now());
                    // update entries in-place to preserve state
                    for (ChecklistEntry entry : checklist.getItems()) {
                        entity.getItems().stream()
                                .filter(e -> e.getEntryId().equals(entry.entryId()))
                                .findFirst()
                                .ifPresent(e -> e.update(entry.confirmed(), entry.confirmedAt(), entry.confirmedNotes()));
                    }
                    return mapper.toDomain(repo.save(entity));
                })
                .orElseGet(() -> mapper.toDomain(repo.save(mapper.toNewEntity(checklist))));
    }

    @Override
    public Optional<EventRiderChecklist> findById(ChecklistId id) {
        return repo.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<EventRiderChecklist> findByEventId(EventId eventId) {
        return repo.findByEventId(eventId.value()).map(mapper::toDomain);
    }

    @Override
    public List<EventRiderChecklist> findChecklistsNeedingAlert(Instant from, Instant to) {
        return repo.findChecklistsNeedingAlert(from, to).stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    public List<EventRiderChecklist> findByPromoterIdAndDateRange(PromoterId promoterId, Instant from, Instant to) {
        return repo.findByPromoterIdAndDateRange(promoterId.value(), from, to).stream()
                .map(mapper::toDomain).toList();
    }
}
