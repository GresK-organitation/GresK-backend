package com.gresk.modules.logistics.infrastructure.persistence.repository;

import com.gresk.modules.logistics.infrastructure.persistence.entity.CrewMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CrewMemberJpaRepository extends JpaRepository<CrewMemberEntity, UUID> {
    Optional<CrewMemberEntity> findByIdAndPromoterId(UUID id, UUID promoterId);
    List<CrewMemberEntity> findByPromoterId(UUID promoterId);
    List<CrewMemberEntity> findByPromoterIdAndActiveTrue(UUID promoterId);
}
