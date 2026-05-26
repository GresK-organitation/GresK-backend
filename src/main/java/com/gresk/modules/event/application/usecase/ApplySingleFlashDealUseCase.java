package com.gresk.modules.event.application.usecase;

import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.event.domain.port.out.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Aplica el flash deal a un único evento en su propia transacción.
 *
 * Bean separado de ApplyFlashDealsUseCase para que @Transactional funcione
 * correctamente a través del proxy de Spring AOP (no se puede anotar un
 * método privado ni una llamada interna con @Transactional).
 *
 * Si este evento falla, solo se revierte su transacción; el resto de
 * candidatos procesados por el scheduler no se ven afectados.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApplySingleFlashDealUseCase {

    private final EventRepository eventRepository;

    @Transactional
    public void execute(Event event) {
        // Verificación final en dominio: protege frente a desfase de reloj entre
        // la consulta SQL del scheduler y el momento real de ejecución.
        if (!event.isFlashDealEligible(Instant.now())) {
            log.warn("Event {} passed SQL filter but is no longer eligible; skipping",
                    event.getId().value());
            return;
        }
        event.applyFlashDeal();
        eventRepository.save(event);
        log.info("Flash deal applied to event {} ({}% off)",
                event.getId().value(), event.getFlashDealDiscountPercent());
    }
}
