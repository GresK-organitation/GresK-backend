package com.gresk.modules.venue.infrastructure.persistence.adapter;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.venue.domain.model.Venue;
import com.gresk.modules.venue.domain.model.VenueId;
import com.gresk.modules.venue.domain.port.out.VenueRepositoryPort;
import com.gresk.modules.venue.infrastructure.persistence.mapper.VenueMapper;
import com.gresk.modules.venue.infrastructure.persistence.repository.VenueJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaVenueRepositoryAdapter implements VenueRepositoryPort {

    private final VenueJpaRepository repo;
    private final VenueMapper        mapper;

    @Override
    @Transactional
    public Venue save(Venue venue) {
        return mapper.toDomain(repo.save(mapper.toEntity(venue)));
    }

    @Override
    public Optional<Venue> findById(VenueId id) {
        return repo.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<Venue> findByOwner(PromoterId ownerId) {
        return repo.findByOwnerId(ownerId.value()).stream().map(mapper::toDomain).toList();
    }
}
