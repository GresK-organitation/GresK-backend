package com.gresk.modules.discovery.infrastructure.scheduler;

import com.gresk.modules.discovery.application.usecase.EnrichArtistsWithMusicBrainzUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class MusicBrainzEnrichmentScheduler {

    private final EnrichArtistsWithMusicBrainzUseCase enrichArtistsWithMusicBrainzUseCase;

    @Scheduled(cron = "0 0 4 1 * *")
    public void enrichMonthly() {
        log.info("MusicBrainz enrichment job triggered at {}", Instant.now());
        enrichArtistsWithMusicBrainzUseCase.execute();
    }
}
