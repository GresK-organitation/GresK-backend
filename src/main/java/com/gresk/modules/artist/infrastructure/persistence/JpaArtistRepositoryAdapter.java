package com.gresk.modules.artist.infrastructure.persistence;

import com.gresk.modules.artist.domain.model.Artist;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.port.out.ArtistRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaArtistRepositoryAdapter implements ArtistRepositoryPort {

    private final ArtistJpaRepository jpaRepository;
    private final ArtistMapper        mapper;

    @Override
    @Transactional
    public Artist save(Artist artist) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(artist)));
    }

    @Override
    public Optional<Artist> findById(ArtistId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<Artist> findByIdAndPromoterId(ArtistId id, PromoterId promoterId) {
        return jpaRepository.findByIdAndPromoterId(id.value(), promoterId.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<Artist> findAllByPromoterId(PromoterId promoterId) {
        return jpaRepository.findByPromoterId(promoterId.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByContactAndPromoterId(String contact, PromoterId promoterId) {
        return jpaRepository.existsByContactAndPromoterId(contact, promoterId.value());
    }

    @Override
    @Transactional
    public void deleteById(ArtistId id) {
        jpaRepository.deleteById(id.value());
    }
}
