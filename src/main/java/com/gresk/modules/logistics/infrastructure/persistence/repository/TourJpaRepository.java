package com.gresk.modules.logistics.infrastructure.persistence.repository;

import com.gresk.modules.logistics.infrastructure.persistence.entity.TourEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TourJpaRepository extends JpaRepository<TourEntity, UUID> {
    Optional<TourEntity> findByIdAndPromoterId(UUID id, UUID promoterId);
    List<TourEntity> findByPromoterId(UUID promoterId);
    List<TourEntity> findByPromoterIdAndStatus(UUID promoterId, String status);
}
