package com.gresk.modules.rider.infrastructure.persistence.repository;

import com.gresk.modules.rider.infrastructure.persistence.entity.TechnicalRiderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TechnicalRiderJpaRepository extends JpaRepository<TechnicalRiderEntity, UUID> {

    List<TechnicalRiderEntity> findByArtistId(UUID artistId);

    Optional<TechnicalRiderEntity> findByShareToken(String shareToken);
}
