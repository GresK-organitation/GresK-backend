package com.gresk.modules.artist.infrastructure.persistence;

import com.gresk.modules.artist.domain.model.Artist;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.port.out.ArtistRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
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
        // For existing entities we MUST update in place — mapper.toEntity() builds a
        // fresh ArtistEntity with version=null, which makes Spring Data call persist()
        // (INSERT) instead of merge() (UPDATE), causing an EntityExistsException.
        // Loading the managed entity and mutating it preserves the @Version field.
        return jpaRepository.findById(artist.getId().value())
                .map(entity -> {
                    entity.updateProfile(
                            artist.getName().value(),
                            artist.getOrigin().value(),
                            new HashSet<>(artist.getGenres()),
                            artist.getImageAssetId().isEmpty() ? null : artist.getImageAssetId().value(),
                            artist.getBio().isEmpty()          ? null : artist.getBio().value()
                    );
                    entity.updateProfessionalInfo(
                            artist.getStatus(),
                            artist.getFee().isEmpty()       ? null : artist.getFee().value(),
                            artist.getFollowers().isEmpty() ? null : artist.getFollowers().value(),
                            new HashSet<>(artist.getTags())
                    );
                    entity.updateContact(
                            artist.getContact().value(),
                            artist.getSocialLinks().hasInstagram() ? artist.getSocialLinks().instagramUrl() : null,
                            artist.getSocialLinks().hasSpotify()   ? artist.getSocialLinks().spotifyUrl()   : null
                    );
                    entity.updateStats(artist.getEventsPlayed(), artist.getAvgRating());
                    return mapper.toDomain(jpaRepository.save(entity));
                })
                // Artist not yet persisted — create from scratch (new entity, version=null is correct)
                .orElseGet(() -> mapper.toDomain(jpaRepository.save(mapper.toEntity(artist))));
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
    public List<Artist> findAllWithSpotifyId() {
        return jpaRepository.findAllWithSpotifyId().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(ArtistId id) {
        jpaRepository.deleteById(id.value());
    }
}
