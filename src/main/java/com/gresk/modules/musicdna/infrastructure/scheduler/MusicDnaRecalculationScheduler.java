package com.gresk.modules.musicdna.infrastructure.scheduler;

import com.gresk.modules.musicdna.application.usecase.RecalculateStaleMusicDnaUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Disparador del job semanal de recálculo del ADN Musical.
 *
 * Responsabilidad única: invocar RecalculateStaleMusicDnaUseCase según el
 * cron configurado. No contiene lógica de negocio — delega completamente
 * en la capa de aplicación.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MusicDnaRecalculationScheduler {

    private final RecalculateStaleMusicDnaUseCase recalculateStaleMusicDnaUseCase;

    @Scheduled(cron = "0 0 4 * * SUN")
    public void recalculateWeekly() {
        log.info("Music DNA weekly recalculation job triggered at {}", Instant.now());
        recalculateStaleMusicDnaUseCase.execute();
    }
}
