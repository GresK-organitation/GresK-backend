package com.gresk.modules.tendencias.chronicle.infrastructure.persistence.repository;

import com.gresk.modules.tendencias.chronicle.domain.model.FeedSourceStatus;
import com.gresk.modules.tendencias.chronicle.infrastructure.persistence.entity.FeedSourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FeedSourceJpaRepository extends JpaRepository<FeedSourceEntity, UUID> {

    boolean existsByFeedUrl(String feedUrl);

    List<FeedSourceEntity> findByStatus(FeedSourceStatus status);
}
