package com.gresk.modules.discovery.infrastructure.persistence.repository;

import com.gresk.modules.discovery.infrastructure.persistence.entity.DemandSignalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface DemandSignalJpaRepository extends JpaRepository<DemandSignalEntity, UUID> {
    Optional<DemandSignalEntity> findByArtistIdAndUserId(UUID artistId, UUID userId);
    long countByArtistId(UUID artistId);
    long countByArtistIdAndCreatedAtBetween(UUID artistId, Instant from, Instant to);
}
