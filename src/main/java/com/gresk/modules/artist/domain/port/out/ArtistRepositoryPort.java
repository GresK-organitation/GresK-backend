package com.gresk.modules.artist.domain.port.out;

import com.gresk.modules.artist.domain.model.Artist;
import com.gresk.modules.artist.domain.model.ArtistId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.util.List;
import java.util.Optional;

public interface ArtistRepositoryPort {
    Artist save(Artist artist);
    Optional<Artist> findById(ArtistId id);
    List<Artist> findByPromoterId(PromoterId promoterId);
    void deleteById(ArtistId id);
}
