package com.gresk.modules.show.infrastructure.persistence.adapter;

import com.gresk.modules.show.domain.model.ShowId;
import com.gresk.modules.show.domain.model.ShowLogEntry;
import com.gresk.modules.show.domain.port.out.ShowLogRepositoryPort;
import com.gresk.modules.show.infrastructure.persistence.mapper.ShowLogEntryMapper;
import com.gresk.modules.show.infrastructure.persistence.repository.ShowLogEntryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaShowLogRepositoryAdapter implements ShowLogRepositoryPort {

    private final ShowLogEntryJpaRepository repo;
    private final ShowLogEntryMapper        mapper;

    @Override
    @Transactional
    public ShowLogEntry save(ShowLogEntry entry) {
        return mapper.toDomain(repo.save(mapper.toEntity(entry)));
    }

    @Override
    public List<ShowLogEntry> findByShowId(ShowId showId) {
        return repo.findByShowIdOrderByOccurredAtAsc(showId.value()).stream().map(mapper::toDomain).toList();
    }
}
