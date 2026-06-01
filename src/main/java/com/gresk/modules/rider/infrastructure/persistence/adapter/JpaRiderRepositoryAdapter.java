package com.gresk.modules.rider.infrastructure.persistence.adapter;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.rider.domain.model.RiderId;
import com.gresk.modules.rider.domain.model.TechnicalRider;
import com.gresk.modules.rider.domain.port.out.RiderRepositoryPort;
import com.gresk.modules.rider.infrastructure.persistence.mapper.TechnicalRiderMapper;
import com.gresk.modules.rider.infrastructure.persistence.repository.TechnicalRiderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaRiderRepositoryAdapter implements RiderRepositoryPort {

    private final TechnicalRiderJpaRepository repo;
    private final TechnicalRiderMapper        mapper;

    @Override
    @Transactional
    public TechnicalRider save(TechnicalRider rider) {
        return mapper.toDomain(repo.save(mapper.toEntity(rider)));
    }

    @Override
    public Optional<TechnicalRider> findById(RiderId id) {
        return repo.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<TechnicalRider> findByShareToken(String shareToken) {
        return repo.findByShareToken(shareToken).map(mapper::toDomain);
    }

    @Override
    public List<TechnicalRider> findByArtistId(ArtistId artistId) {
        return repo.findByArtistId(artistId.value()).stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsById(RiderId id) {
        return repo.existsById(id.value());
    }
}
