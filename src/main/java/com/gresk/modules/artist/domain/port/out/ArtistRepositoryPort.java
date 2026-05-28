package com.gresk.modules.artist.domain.port.out;

import com.gresk.modules.artist.domain.model.Artist;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.util.List;
import java.util.Optional;

public interface ArtistRepositoryPort {

    Artist save(Artist artist);

    Optional<Artist> findById(ArtistId id);

    Optional<Artist> findByIdAndPromoterId(ArtistId id, PromoterId promoterId);

    List<Artist> findAllByPromoterId(PromoterId promoterId);

    boolean existsByContactAndPromoterId(String contact, PromoterId promoterId);

    List<Artist> findAllWithSpotifyId();

    void deleteById(ArtistId id);
}
