package com.gresk.modules.artist.infrastructure.persistence;

import com.gresk.modules.artist.domain.model.ArtistMetricsSnapshot;
import com.gresk.modules.artist.domain.port.out.ArtistMetricsSnapshotRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Repository
@RequiredArgsConstructor
public class JpaArtistMetricsSnapshotRepositoryAdapter implements ArtistMetricsSnapshotRepositoryPort {

    private final ArtistMetricsSnapshotJpaRepository jpaRepository;

    @Override
    @Transactional
    public void save(ArtistMetricsSnapshot snapshot) {
        jpaRepository.save(toEntity(snapshot));
    }

    @Override
    @Transactional
    public void deleteOlderThan(LocalDate cutoff) {
        jpaRepository.deleteBySnapshotDateBefore(cutoff);
    }

    private ArtistMetricsSnapshotEntity toEntity(ArtistMetricsSnapshot s) {
        return ArtistMetricsSnapshotEntity.builder()
                .id(s.id())
                .artistId(s.artistId().value())
                .snapshotDate(s.snapshotDate())
                .spotifyPopularity(s.spotifyPopularity())
                .spotifyFollowers(s.spotifyFollowers())
                .lastReleaseDate(s.lastReleaseDate())
                .totalReleases(s.totalReleases())
                .createdAt(s.createdAt())
                .build();
    }
}
