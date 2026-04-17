package com.gresk.modules.artist.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArtistJpaRepository extends JpaRepository<ArtistEntity, UUID> {

    List<ArtistEntity> findByPromoterId(UUID promoterId);

    Optional<ArtistEntity> findByIdAndPromoterId(UUID id, UUID promoterId);

    boolean existsByContactAndPromoterId(String contact, UUID promoterId);
}
