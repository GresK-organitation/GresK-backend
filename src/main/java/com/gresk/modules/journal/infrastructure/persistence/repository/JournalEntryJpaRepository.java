package com.gresk.modules.journal.infrastructure.persistence.repository;

import com.gresk.modules.journal.infrastructure.persistence.entity.JournalEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface JournalEntryJpaRepository
        extends JpaRepository<JournalEntryEntity, UUID>,
                JpaSpecificationExecutor<JournalEntryEntity> {
}
