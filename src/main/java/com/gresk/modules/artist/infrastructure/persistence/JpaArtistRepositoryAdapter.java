package com.gresk.modules.artist.infrastructure.persistence;

import com.gresk.modules.artist.domain.model.Artist;
import com.gresk.modules.artist.domain.model.ArtistId;
import com.gresk.modules.artist.domain.port.out.ArtistRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaArtistRepositoryAdapter implements ArtistRepositoryPort {

    private final JpaArtistRepository jpaRepository;
    private final ArtistMapper        mapper;

    @Override
    public Artist save(Artist artist) {
        ArtistEntity entity = jpaRepository.findById(artist.getId().value())
                .map(existing -> {
                    existing.update(
                            artist.getName(), artist.getOrigin(), artist.getGenres(),
                            artist.getImageUrl(), artist.getBio(), artist.getStatus(),
                            artist.getFee(), artist.getFollowers(), artist.getContact(),
                            artist.getSocialSpotify(), artist.getSocialInstagram(), artist.getTags()
                    );
                    return existing;
                })
                .orElseGet(() -> mapper.toEntity(artist));
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Artist> findById(ArtistId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<Artist> findByPromoterId(PromoterId promoterId) {
        return jpaRepository.findByPromoterId(promoterId.value())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(ArtistId id) {
        jpaRepository.deleteById(id.value());
    }
}
