package com.gresk.modules.agenda.domain.port.out;

import com.gresk.modules.agenda.domain.model.AgendaGresKEvent;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.time.Instant;
import java.util.List;

/**
 * Puerto de salida que desacopla el módulo agenda del módulo event.
 * Su implementación en infraestructura consulta el repositorio JPA del módulo event.
 */
public interface PromoterEventQueryPort {

    List<AgendaGresKEvent> findByPromoterAndDateRange(PromoterId promoterId, Instant from, Instant to);
}
