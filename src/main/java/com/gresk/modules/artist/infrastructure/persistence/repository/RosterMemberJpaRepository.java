package com.gresk.modules.artist.infrastructure.persistence.repository;

import com.gresk.modules.artist.infrastructure.persistence.entity.RosterMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RosterMemberJpaRepository extends JpaRepository<RosterMemberEntity, UUID> {
    Optional<RosterMemberEntity> findByIdAndPromoterId(UUID id, UUID promoterId);
    List<RosterMemberEntity> findByArtistId(UUID artistId);
}
