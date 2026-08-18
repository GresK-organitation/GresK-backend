package com.gresk.modules.tendencias.chronicle.infrastructure.persistence.repository;

import com.gresk.modules.tendencias.chronicle.domain.model.ChronicleStatus;
import com.gresk.modules.tendencias.chronicle.infrastructure.persistence.entity.ChronicleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChronicleJpaRepository extends JpaRepository<ChronicleEntity, UUID> {

    boolean existsByFeedSourceIdAndGuid(UUID feedSourceId, String guid);

    Page<ChronicleEntity> findByStatusOrderByOriginalPublishedAtDesc(ChronicleStatus status, Pageable pageable);
}
