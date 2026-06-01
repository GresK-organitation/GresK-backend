package com.gresk.modules.rider.infrastructure.persistence.repository;

import com.gresk.modules.rider.infrastructure.persistence.entity.RiderAlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RiderAlertJpaRepository extends JpaRepository<RiderAlertEntity, UUID> {

    List<RiderAlertEntity> findByPromoterIdAndReadFalseOrderByCreatedAtDesc(UUID promoterId);

    long countByPromoterIdAndReadFalse(UUID promoterId);
}
