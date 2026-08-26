package com.gresk.modules.discovery.infrastructure.persistence.adapter;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.discovery.domain.model.ArtistDiscoveryProfile;
import com.gresk.modules.discovery.domain.port.out.ArtistDiscoveryProfileRepository;
import com.gresk.modules.discovery.infrastructure.persistence.entity.ArtistDiscoveryProfileEntity;
import com.gresk.modules.discovery.infrastructure.persistence.mapper.ArtistDiscoveryProfileMapper;
import com.gresk.modules.discovery.infrastructure.persistence.repository.ArtistDiscoveryProfileJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaArtistDiscoveryProfileRepositoryAdapter implements ArtistDiscoveryProfileRepository {

    private final ArtistDiscoveryProfileJpaRepository repo;
    private final ArtistDiscoveryProfileMapper mapper;

    @Override
    @Transactional
    public ArtistDiscoveryProfile save(ArtistDiscoveryProfile profile) {
        ArtistDiscoveryProfileEntity entity = mapper.toEntity(profile);
        // Upsert por artist_id: el agregado siempre genera un id nuevo al
        // recalcularse — reusamos el id de la fila existente (si la hay)
        // para que Hibernate haga UPDATE en vez de violar el UNIQUE.
        repo.findByArtistId(profile.getArtistId().value())
                .ifPresent(existing -> entity.setId(existing.getId()));
        return mapper.toDomain(repo.save(entity));
    }

    @Override
    public Optional<ArtistDiscoveryProfile> findByArtistId(ArtistId artistId) {
        return repo.findByArtistId(artistId.value()).map(mapper::toDomain);
    }
}
