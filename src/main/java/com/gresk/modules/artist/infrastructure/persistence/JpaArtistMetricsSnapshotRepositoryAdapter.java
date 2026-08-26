package com.gresk.modules.artist.infrastructure.persistence;

import com.gresk.modules.artist.domain.model.ArtistMetricsSnapshot;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.port.out.ArtistMetricsSnapshotRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

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

    @Override
    public Optional<ArtistMetricsSnapshot> findLatestByArtistId(ArtistId artistId) {
        return jpaRepository.findFirstByArtistIdOrderBySnapshotDateDesc(artistId.value())
                .map(this::toDomain);
    }

    private ArtistMetricsSnapshot toDomain(ArtistMetricsSnapshotEntity e) {
        return new ArtistMetricsSnapshot(
                e.getId(), ArtistId.of(e.getArtistId()), e.getSnapshotDate(),
                e.getSpotifyPopularity(), e.getSpotifyFollowers(),
                e.getLastReleaseDate(), e.getTotalReleases(), e.getCreatedAt()
        );
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
