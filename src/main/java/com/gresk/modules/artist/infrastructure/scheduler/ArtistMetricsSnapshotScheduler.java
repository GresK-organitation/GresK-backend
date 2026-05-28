package com.gresk.modules.artist.infrastructure.scheduler;

import com.gresk.modules.artist.application.port.in.CollectArtistMetricsPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Disparador del job periódico de métricas de artistas.
 *
 * Responsabilidad única: invocar CollectArtistMetricsPort según el cron configurado.
 * No contiene lógica de negocio — delega completamente en la capa de aplicación.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ArtistMetricsSnapshotScheduler {

    private static final int RETENTION_DAYS = 365;

    private final CollectArtistMetricsPort collectArtistMetrics;

    @Scheduled(cron = "0 0 3 */3 * *")
    public void collectMetrics() {
        log.info("Artist metrics snapshot job triggered at {}", Instant.now());
        collectArtistMetrics.collectMetrics();
    }

    @Scheduled(cron = "0 0 4 1 * *")
    public void purgeOldMetrics() {
        log.info("Artist metrics purge job triggered at {}", Instant.now());
        collectArtistMetrics.purgeOlderThan(RETENTION_DAYS);
    }
}
