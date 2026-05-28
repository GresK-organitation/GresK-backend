package com.gresk.modules.artist.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface ArtistMetricsSnapshotJpaRepository
        extends JpaRepository<ArtistMetricsSnapshotEntity, UUID> {

    @Modifying
    @Query("DELETE FROM ArtistMetricsSnapshotEntity s WHERE s.snapshotDate < :cutoff")
    void deleteBySnapshotDateBefore(@Param("cutoff") LocalDate cutoff);

    Optional<ArtistMetricsSnapshotEntity> findFirstByArtistIdOrderBySnapshotDateDesc(UUID artistId);

    Optional<ArtistMetricsSnapshotEntity> findFirstByArtistIdAndSnapshotDateBeforeOrderBySnapshotDateDesc(
            UUID artistId, LocalDate before);
}
