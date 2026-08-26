package com.gresk.modules.discovery.application.usecase;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.discovery.domain.model.ArtistDiscoveryProfile;
import com.gresk.modules.discovery.domain.port.out.ArtistCatalogInfo;
import com.gresk.modules.discovery.domain.port.out.ArtistCatalogPort;
import com.gresk.modules.discovery.domain.port.out.ArtistDiscoveryProfileRepository;
import com.gresk.modules.discovery.domain.port.out.MusicBrainzArtistInfo;
import com.gresk.modules.discovery.domain.port.out.MusicBrainzLookupPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Job mensual: enriquece con datos de MusicBrainz (país/ciudad/año de
 * formación) los artistas que ya tienen perfil de Discovery pero aún no
 * tienen musicbrainz_id. Best-effort — un fallo aislado no bloquea el resto.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EnrichArtistsWithMusicBrainzUseCase {

    private final ArtistCatalogPort artistCatalogPort;
    private final ArtistDiscoveryProfileRepository profileRepository;
    private final MusicBrainzLookupPort musicBrainzLookupPort;

    public void execute() {
        for (ArtistId artistId : artistCatalogPort.findAllArtistIds()) {
            try {
                enrichOne(artistId);
            } catch (Exception e) {
                log.error("Failed to enrich artist {} with MusicBrainz: {}", artistId, e.getMessage());
            }
        }
    }

    @Transactional
    void enrichOne(ArtistId artistId) {
        var existingProfile = profileRepository.findByArtistId(artistId);
        if (existingProfile.isEmpty() || existingProfile.get().getMusicBrainzId() != null) return;

        ArtistCatalogInfo catalogInfo = artistCatalogPort.findById(artistId).orElse(null);
        if (catalogInfo == null) return;

        MusicBrainzArtistInfo mbInfo = musicBrainzLookupPort.searchByName(catalogInfo.name()).orElse(null);
        if (mbInfo == null) return;

        ArtistDiscoveryProfile updated = existingProfile.get().withMusicBrainzInfo(
                mbInfo.musicBrainzId(), mbInfo.country(), mbInfo.city(), mbInfo.beginYear());
        profileRepository.save(updated);
    }
}
