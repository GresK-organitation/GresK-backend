package com.gresk.modules.artist.infrastructure.persistence.adapter;

import com.gresk.modules.artist.domain.model.ArtistTractionSnapshot;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.port.out.ArtistTractionSnapshotRepositoryPort;
import com.gresk.modules.artist.infrastructure.persistence.mapper.ArtistTractionSnapshotMapper;
import com.gresk.modules.artist.infrastructure.persistence.repository.ArtistTractionSnapshotJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaArtistTractionSnapshotRepositoryAdapter implements ArtistTractionSnapshotRepositoryPort {

    private final ArtistTractionSnapshotJpaRepository jpaRepository;
    private final ArtistTractionSnapshotMapper        mapper;

    @Override
    @Transactional
    public void save(ArtistTractionSnapshot snapshot) {
        jpaRepository.save(mapper.toEntity(snapshot));
    }

    @Override
    @Transactional
    public void deleteOlderThan(LocalDate cutoff) {
        jpaRepository.deleteBySnapshotDateBefore(cutoff);
    }

    @Override
    public Optional<ArtistTractionSnapshot> findLatestByArtistId(ArtistId artistId) {
        return jpaRepository.findTopByArtistIdOrderBySnapshotDateDesc(artistId.value()).map(mapper::toDomain);
    }
}
