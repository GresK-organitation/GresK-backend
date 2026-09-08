package com.gresk.modules.artist.infrastructure.persistence.repository;

import com.gresk.modules.artist.infrastructure.persistence.entity.ArtistTractionSnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface ArtistTractionSnapshotJpaRepository extends JpaRepository<ArtistTractionSnapshotEntity, UUID> {
    Optional<ArtistTractionSnapshotEntity> findTopByArtistIdOrderBySnapshotDateDesc(UUID artistId);
    void deleteBySnapshotDateBefore(LocalDate cutoff);
}
