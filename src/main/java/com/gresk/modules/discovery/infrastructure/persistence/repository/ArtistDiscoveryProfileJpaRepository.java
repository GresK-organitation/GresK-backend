package com.gresk.modules.discovery.infrastructure.persistence.repository;

import com.gresk.modules.discovery.infrastructure.persistence.entity.ArtistDiscoveryProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ArtistDiscoveryProfileJpaRepository extends JpaRepository<ArtistDiscoveryProfileEntity, UUID> {
    Optional<ArtistDiscoveryProfileEntity> findByArtistId(UUID artistId);
}
