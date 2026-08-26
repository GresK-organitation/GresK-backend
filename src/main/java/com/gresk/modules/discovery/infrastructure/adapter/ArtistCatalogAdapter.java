package com.gresk.modules.discovery.infrastructure.adapter;

import com.gresk.modules.artist.domain.model.Artist;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.port.out.ArtistRepositoryPort;
import com.gresk.modules.discovery.domain.port.out.ArtistCatalogInfo;
import com.gresk.modules.discovery.domain.port.out.ArtistCatalogPort;
import com.gresk.shared.domain.MusicGenre;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Lookup de solo lectura al catálogo de artistas del módulo `artist`. Nunca
 * escribe, nunca toca métricas oficiales — mismo criterio que
 * {@code journal.infrastructure.adapter.ArtistLookupAdapter}.
 */
@Component
@RequiredArgsConstructor
public class ArtistCatalogAdapter implements ArtistCatalogPort {

    private final ArtistRepositoryPort artistRepositoryPort;

    @Override
    public Optional<ArtistCatalogInfo> findById(ArtistId artistId) {
        return artistRepositoryPort.findById(artistId).map(this::toInfo);
    }

    @Override
    public List<ArtistId> findAllArtistIds() {
        return artistRepositoryPort.findAll().stream().map(Artist::getId).toList();
    }

    private ArtistCatalogInfo toInfo(Artist artist) {
        return new ArtistCatalogInfo(
                artist.getId().toString(),
                artist.getName().value(),
                artist.getOrigin().value(),
                artist.getGenres().stream().map(MusicGenre::name).toList(),
                artist.getImageAssetId().value(),
                artist.getBio().value(),
                artist.getAvgRating(),
                artist.getSpotifyProfile().artistId(),
                artist.getSocialLinks().spotifyUrl(),
                null,
                artist.getCreatedAt()
        );
    }
}
