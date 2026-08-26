package com.gresk.modules.discovery.infrastructure.scheduler;

import com.gresk.modules.discovery.application.usecase.RecalculateAllDiscoveryProfilesUseCase;
import com.gresk.modules.discovery.application.usecase.RefreshUpcomingEventFlagsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Disparadores de los jobs de Discovery. Responsabilidad única: invocar los
 * casos de uso según el cron configurado — sin lógica de negocio.
 *
 * El recálculo completo va los martes (escalonado respecto a musicdna, que
 * corre en domingo, y artist, que corre cada 3 días empezando el lunes).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DiscoveryProfileRecalculationScheduler {

    private final RecalculateAllDiscoveryProfilesUseCase recalculateAllDiscoveryProfilesUseCase;
    private final RefreshUpcomingEventFlagsUseCase refreshUpcomingEventFlagsUseCase;

    @Scheduled(cron = "0 0 3 * * TUE")
    public void recalculateWeekly() {
        log.info("Discovery profile weekly recalculation job triggered at {}", Instant.now());
        recalculateAllDiscoveryProfilesUseCase.execute();
    }

    @Scheduled(cron = "0 0 6 * * *")
    public void refreshUpcomingEventFlagsDaily() {
        log.info("Discovery upcoming event flags daily refresh triggered at {}", Instant.now());
        refreshUpcomingEventFlagsUseCase.execute();
    }
}
