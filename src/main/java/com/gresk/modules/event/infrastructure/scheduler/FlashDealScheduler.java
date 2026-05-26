package com.gresk.modules.event.infrastructure.scheduler;

import com.gresk.modules.event.application.usecase.ApplyFlashDealsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Disparador de la tarea periódica del flash deal.
 *
 * Responsabilidad única: invocar ApplyFlashDealsUseCase cada hora.
 * No contiene lógica de negocio — delega completamente en la capa de aplicación.
 *
 * fixedRate = 3_600_000 ms (60 min): se dispara cada hora desde el arranque
 * de la aplicación, independientemente de cuánto tarde la ejecución anterior.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FlashDealScheduler {

    private final ApplyFlashDealsUseCase applyFlashDealsUseCase;

    @Scheduled(fixedRate = 3_600_000)
    public void applyFlashDeals() {
        log.info("Flash deal scheduler triggered at {}", Instant.now());
        applyFlashDealsUseCase.execute();
    }
}
