package com.gresk.modules.logistics.infrastructure.persistence.repository;

import com.gresk.modules.logistics.infrastructure.persistence.entity.RoomingListEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomingListJpaRepository extends JpaRepository<RoomingListEntity, UUID> {
    Optional<RoomingListEntity> findByIdAndPromoterId(UUID id, UUID promoterId);
    List<RoomingListEntity> findByTourId(UUID tourId);
}
