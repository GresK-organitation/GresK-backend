package com.gresk.modules.artist.infrastructure.scheduler;

import com.gresk.modules.artist.application.port.in.CollectBandsintownTractionPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Calcado de ArtistMetricsSnapshotScheduler, cada 3 días. */
@Component
@RequiredArgsConstructor
@Slf4j
public class BandsintownTractionScheduler {

    private static final int RETENTION_DAYS = 365;

    private final CollectBandsintownTractionPort collectTraction;

    @Scheduled(cron = "0 0 5 */3 * *")
    public void collectTraction() {
        log.info("Bandsintown traction snapshot job triggered");
        collectTraction.execute();
    }

    @Scheduled(cron = "0 30 4 1 * *")
    public void purgeOldSnapshots() {
        log.info("Bandsintown traction purge job triggered");
        collectTraction.purgeOlderThan(RETENTION_DAYS);
    }
}
