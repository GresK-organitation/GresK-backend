package com.gresk.modules.logistics.infrastructure.persistence.repository;

import com.gresk.modules.logistics.infrastructure.persistence.entity.ItineraryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ItineraryJpaRepository extends JpaRepository<ItineraryEntity, UUID> {
    Optional<ItineraryEntity> findByIdAndPromoterId(UUID id, UUID promoterId);
    Optional<ItineraryEntity> findByTourId(UUID tourId);
}
