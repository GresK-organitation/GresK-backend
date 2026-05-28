package com.gresk.modules.artist.application.usecase;

import com.gresk.modules.artist.application.dto.SpotifyArtistMetricsDTO;
import com.gresk.modules.artist.application.port.in.CollectArtistMetricsPort;
import com.gresk.modules.artist.domain.model.Artist;
import com.gresk.modules.artist.domain.model.ArtistMetricsSnapshot;
import com.gresk.modules.artist.domain.port.out.ArtistMetricsSnapshotRepositoryPort;
import com.gresk.modules.artist.domain.port.out.ArtistRepositoryPort;
import com.gresk.modules.artist.domain.port.out.SpotifyArtistMetricsPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Orquesta la recopilación de métricas de Spotify para todos los artistas vinculados.
 * Invocado exclusivamente por ArtistMetricsSnapshotScheduler.
 *
 * No es @Transactional a nivel de clase: cada snapshot se guarda en su propia
 * transacción para que un fallo en un artista no revierta los demás.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CollectArtistMetricsUseCase implements CollectArtistMetricsPort {

    private final ArtistRepositoryPort                artistRepository;
    private final SpotifyArtistMetricsPort            spotifyMetrics;
    private final ArtistMetricsSnapshotRepositoryPort snapshotRepository;

    @Override
    public void collectMetrics() {
        List<Artist> artists = artistRepository.findAllWithSpotifyId();
        log.info("Artist metrics job: {} artist(s) with Spotify ID found", artists.size());

        for (Artist artist : artists) {
            try {
                SpotifyArtistMetricsDTO metrics = spotifyMetrics.fetchMetrics(
                        artist.getSpotifyProfile().artistId()
                );
                ArtistMetricsSnapshot snapshot = ArtistMetricsSnapshot.create(
                        artist.getId(),
                        metrics.popularity(),
                        metrics.followers(),
                        metrics.lastReleaseDate(),
                        metrics.totalReleases()
                );
                snapshotRepository.save(snapshot);
                log.info("Saved metrics snapshot for artist '{}'", artist.getName().value());
            } catch (Exception e) {
                log.error("Failed to collect metrics for artist '{}' ({}): {}",
                        artist.getName().value(), artist.getId().value(), e.getMessage());
            }
        }
    }

    @Override
    public void purgeOlderThan(int days) {
        LocalDate cutoff = LocalDate.now().minusDays(days);
        snapshotRepository.deleteOlderThan(cutoff);
        log.info("Purged artist metrics snapshots older than {}", cutoff);
    }
}
