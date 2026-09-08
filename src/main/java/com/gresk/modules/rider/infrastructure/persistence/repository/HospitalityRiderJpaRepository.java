package com.gresk.modules.rider.infrastructure.persistence.repository;

import com.gresk.modules.rider.infrastructure.persistence.entity.HospitalityRiderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HospitalityRiderJpaRepository extends JpaRepository<HospitalityRiderEntity, UUID> {

    List<HospitalityRiderEntity> findByArtistId(UUID artistId);

    Optional<HospitalityRiderEntity> findByShareToken(String shareToken);
}
