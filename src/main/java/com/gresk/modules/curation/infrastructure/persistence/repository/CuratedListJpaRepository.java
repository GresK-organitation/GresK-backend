package com.gresk.modules.curation.infrastructure.persistence.repository;

import com.gresk.modules.curation.infrastructure.persistence.entity.CuratedListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface CuratedListJpaRepository
        extends JpaRepository<CuratedListEntity, UUID>,
                JpaSpecificationExecutor<CuratedListEntity> {
}
