package com.gresk.modules.artist.infrastructure.persistence;

import com.gresk.shared.domain.MusicGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ArtistJpaRepository extends JpaRepository<ArtistEntity, UUID> {

    List<ArtistEntity> findByPromoterId(UUID promoterId);

    Optional<ArtistEntity> findByIdAndPromoterId(UUID id, UUID promoterId);

    boolean existsByContactAndPromoterId(String contact, UUID promoterId);

    @Query("SELECT a FROM ArtistEntity a WHERE a.spotifyArtistId IS NOT NULL")
    List<ArtistEntity> findAllWithSpotifyId();

    @Query("SELECT DISTINCT a FROM ArtistEntity a JOIN a.genres g WHERE g IN :genres AND a.spotifyArtistId IS NOT NULL")
    List<ArtistEntity> findByGenresAndHasSpotifyId(@Param("genres") Set<MusicGenre> genres);
}
