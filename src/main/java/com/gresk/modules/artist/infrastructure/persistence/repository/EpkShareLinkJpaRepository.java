package com.gresk.modules.artist.infrastructure.persistence.repository;

import com.gresk.modules.artist.infrastructure.persistence.entity.EpkShareLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EpkShareLinkJpaRepository extends JpaRepository<EpkShareLinkEntity, UUID> {
    Optional<EpkShareLinkEntity> findByToken(String token);
    List<EpkShareLinkEntity> findByEpkAssetId(UUID epkAssetId);
    void deleteByExpiresAtBeforeAndRevokedFalse(Instant cutoff);
}
