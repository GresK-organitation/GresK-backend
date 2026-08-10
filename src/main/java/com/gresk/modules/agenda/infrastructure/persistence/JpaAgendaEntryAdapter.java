package com.gresk.modules.agenda.infrastructure.persistence;

import com.gresk.modules.agenda.domain.model.AgendaEntry;
import com.gresk.modules.agenda.domain.model.AgendaEntryId;
import com.gresk.modules.agenda.domain.port.out.AgendaEntryRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaAgendaEntryAdapter implements AgendaEntryRepository {

    private final AgendaEntryJpaRepository repo;
    private final AgendaEntryMapper        mapper;

    @Override
    @Transactional
    public AgendaEntry save(AgendaEntry entry) {
        return mapper.toDomain(repo.save(mapper.toEntity(entry)));
    }

    @Override
    public Optional<AgendaEntry> findById(AgendaEntryId id) {
        return repo.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<AgendaEntry> findSimpleByPromoterAndDateRange(PromoterId promoterId, Instant from, Instant to) {
        return repo.findSimpleByPromoterAndDateRange(promoterId.value(), from, to)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<AgendaEntry> findMastersWithPotentialOccurrences(PromoterId promoterId, Instant from, Instant to) {
        return repo.findMastersWithPotentialOccurrences(promoterId.value(), from, to)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<AgendaEntry> findExceptionsBySeriesId(AgendaEntryId seriesId) {
        return repo.findBySeriesId(seriesId.value())
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<AgendaEntry> findExceptionsBySeriesIdFromDate(AgendaEntryId seriesId, Instant fromDate) {
        return repo.findExceptionsBySeriesIdFromDate(seriesId.value(), fromDate)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public void deleteById(AgendaEntryId id) {
        repo.deleteById(id.value());
    }

    @Override
    @Transactional
    public void deleteExceptionsBySeriesIdFromDate(AgendaEntryId seriesId, Instant fromDate) {
        repo.deleteExceptionsBySeriesIdFromDate(seriesId.value(), fromDate);
    }

    @Override
    @Transactional
    public void deleteAllExceptionsBySeriesId(AgendaEntryId seriesId) {
        repo.deleteAllExceptionsBySeriesId(seriesId.value());
    }

    @Override
    public List<AgendaEntry> findPendingReminders(Instant now) {
        return repo.findPendingReminders(now)
                .stream().map(mapper::toDomain).toList();
    }
}
