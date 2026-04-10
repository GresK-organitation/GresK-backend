package com.gresk.modules.artist.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaArtistRepository extends JpaRepository<ArtistEntity, UUID> {
    List<ArtistEntity> findByPromoterId(UUID promoterId);
}
