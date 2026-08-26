package com.gresk.modules.discovery.infrastructure.persistence.adapter;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.discovery.domain.model.DemandSignal;
import com.gresk.modules.discovery.domain.port.out.DemandSignalRepository;
import com.gresk.modules.discovery.infrastructure.persistence.mapper.DemandSignalMapper;
import com.gresk.modules.discovery.infrastructure.persistence.repository.DemandSignalJpaRepository;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaDemandSignalRepositoryAdapter implements DemandSignalRepository {

    private final DemandSignalJpaRepository repo;
    private final DemandSignalMapper mapper;

    @Override
    @Transactional
    public DemandSignal save(DemandSignal signal) {
        return mapper.toDomain(repo.save(mapper.toEntity(signal)));
    }

    @Override
    public Optional<DemandSignal> findByArtistAndUser(ArtistId artistId, UserId userId) {
        return repo.findByArtistIdAndUserId(artistId.value(), userId.value()).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void delete(DemandSignal signal) {
        repo.deleteById(signal.getId().value());
    }

    @Override
    public long countByArtist(ArtistId artistId) {
        return repo.countByArtistId(artistId.value());
    }

    @Override
    public long countByArtistAndPeriod(ArtistId artistId, Instant from, Instant to) {
        return repo.countByArtistIdAndCreatedAtBetween(artistId.value(), from, to);
    }
}
