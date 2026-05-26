package com.gresk.modules.event.application.usecase;

import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.event.domain.port.out.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Orquesta la aplicación del flash deal a todos los eventos elegibles.
 * Invocado exclusivamente por FlashDealScheduler.
 *
 * No es @Transactional: la transacción se abre por evento en ApplySingleFlashDealUseCase,
 * garantizando que un fallo en un evento no revierta los demás.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApplyFlashDealsUseCase {

    private final EventRepository          eventRepository;
    private final ApplySingleFlashDealUseCase applySingle;

    public void execute() {
        List<Event> candidates = eventRepository.findEligibleForFlashDeal();
        log.info("Flash deal scheduler: {} candidate(s) found", candidates.size());

        for (Event event : candidates) {
            try {
                applySingle.execute(event);
            } catch (Exception e) {
                log.error("Failed to apply flash deal to event {}: {}",
                        event.getId().value(), e.getMessage(), e);
            }
        }
    }
}
