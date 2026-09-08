package com.gresk.modules.logistics.infrastructure.persistence.repository;

import com.gresk.modules.logistics.infrastructure.persistence.entity.TravelPartyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TravelPartyJpaRepository extends JpaRepository<TravelPartyEntity, UUID> {
    Optional<TravelPartyEntity> findByIdAndPromoterId(UUID id, UUID promoterId);
    Optional<TravelPartyEntity> findByTourId(UUID tourId);
}
