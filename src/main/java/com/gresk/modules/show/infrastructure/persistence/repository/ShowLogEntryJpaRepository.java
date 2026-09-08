package com.gresk.modules.show.infrastructure.persistence.repository;

import com.gresk.modules.show.infrastructure.persistence.entity.ShowLogEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ShowLogEntryJpaRepository extends JpaRepository<ShowLogEntryEntity, UUID> {
    List<ShowLogEntryEntity> findByShowIdOrderByOccurredAtAsc(UUID showId);
}
