package com.gresk.modules.venue.infrastructure.persistence.repository;

import com.gresk.modules.venue.infrastructure.persistence.entity.VenueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VenueJpaRepository extends JpaRepository<VenueEntity, UUID> {
    List<VenueEntity> findByOwnerId(UUID ownerId);
}
