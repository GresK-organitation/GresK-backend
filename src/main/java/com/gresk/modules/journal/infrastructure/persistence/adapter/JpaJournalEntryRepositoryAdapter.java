package com.gresk.modules.journal.infrastructure.persistence.adapter;

import com.gresk.modules.journal.domain.model.JournalEntry;
import com.gresk.modules.journal.domain.model.JournalEntryId;
import com.gresk.modules.journal.domain.port.out.JournalEntryFilter;
import com.gresk.modules.journal.domain.port.out.JournalEntryRepository;
import com.gresk.modules.journal.infrastructure.persistence.mapper.JournalEntryMapper;
import com.gresk.modules.journal.infrastructure.persistence.repository.JournalEntryJpaRepository;
import com.gresk.modules.journal.infrastructure.persistence.repository.JournalEntrySpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaJournalEntryRepositoryAdapter implements JournalEntryRepository {

    private final JournalEntryJpaRepository repo;
    private final JournalEntryMapper        mapper;

    @Override
    @Transactional
    public JournalEntry save(JournalEntry entry) {
        return mapper.toDomain(repo.save(mapper.toEntity(entry)));
    }

    @Override
    public Optional<JournalEntry> findById(JournalEntryId id) {
        return repo.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<JournalEntry> findAll(JournalEntryFilter filter, PageRequest pageRequest) {
        return repo.findAll(JournalEntrySpecifications.fromFilter(filter), pageRequest)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public long count(JournalEntryFilter filter) {
        return repo.count(JournalEntrySpecifications.fromFilter(filter));
    }

    @Override
    @Transactional
    public void deleteById(JournalEntryId id) {
        repo.deleteById(id.value());
    }
}
