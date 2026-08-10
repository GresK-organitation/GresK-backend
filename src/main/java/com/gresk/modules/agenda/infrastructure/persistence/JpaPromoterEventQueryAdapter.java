package com.gresk.modules.agenda.infrastructure.persistence;

import com.gresk.modules.agenda.domain.model.AgendaGresKEvent;
import com.gresk.modules.agenda.domain.port.out.PromoterEventQueryPort;
import com.gresk.modules.event.infrastructure.persistence.EventEntity;
import com.gresk.modules.event.infrastructure.persistence.EventJpaRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Implementación del puerto PromoterEventQueryPort que consulta directamente
 * el repositorio JPA del módulo event. La dependencia es exclusivamente a
 * nivel de infraestructura — el dominio de agenda no conoce el dominio de event.
 */
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaPromoterEventQueryAdapter implements PromoterEventQueryPort {

    private final EventJpaRepository eventRepo;

    @Override
    public List<AgendaGresKEvent> findByPromoterAndDateRange(PromoterId promoterId, Instant from, Instant to) {
        return eventRepo.findByPromoterAndDateRange(promoterId.value(), from, to)
                .stream()
                .map(this::toAgendaItem)
                .toList();
    }

    private AgendaGresKEvent toAgendaItem(EventEntity e) {
        int soldPercentage = 0;
        if (e.getTotalCapacity() != null && e.getTotalCapacity() > 0) {
            int available = e.getAvailableCapacity() != null ? e.getAvailableCapacity() : 0;
            soldPercentage = (int) Math.round(
                    (e.getTotalCapacity() - available) / (double) e.getTotalCapacity() * 100
            );
        }
        return new AgendaGresKEvent(
                e.getId().toString(),
                e.getTitle(),
                e.getEventDate(),
                e.getStatus()  != null ? e.getStatus().name()  : null,
                e.getGenre()   != null ? e.getGenre().name()   : null,
                e.getCity(),
                e.getVenue(),
                soldPercentage
        );
    }
}
