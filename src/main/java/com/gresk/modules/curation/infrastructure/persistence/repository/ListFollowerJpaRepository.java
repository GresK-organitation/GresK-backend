package com.gresk.modules.curation.infrastructure.persistence.repository;

import com.gresk.modules.curation.infrastructure.persistence.entity.ListFollowerEntity;
import com.gresk.modules.curation.infrastructure.persistence.entity.ListFollowerId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface ListFollowerJpaRepository extends JpaRepository<ListFollowerEntity, ListFollowerId> {

    boolean existsByList_IdAndUserId(UUID listId, UUID userId);

    @Transactional
    void deleteByList_IdAndUserId(UUID listId, UUID userId);

    long countByList_Id(UUID listId);

    List<ListFollowerEntity> findByList_Id(UUID listId, Pageable pageable);

    long countByUserId(UUID userId);

    List<ListFollowerEntity> findByUserId(UUID userId, Pageable pageable);
}
