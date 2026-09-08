package com.gresk.modules.artist.infrastructure.persistence.repository;

import com.gresk.modules.artist.infrastructure.persistence.entity.BandMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BandMemberJpaRepository extends JpaRepository<BandMemberEntity, UUID> {
    Optional<BandMemberEntity> findByIdAndPromoterId(UUID id, UUID promoterId);
    List<BandMemberEntity> findByArtistId(UUID artistId);
    List<BandMemberEntity> findByActiveTrue();
}
