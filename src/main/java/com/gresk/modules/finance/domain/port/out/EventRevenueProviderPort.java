package com.gresk.modules.finance.domain.port.out;

import com.gresk.modules.finance.domain.exception.EventNotFoundException;
import com.gresk.modules.finance.domain.model.valueobject.EventRevenueSnapshot;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.util.UUID;

/**
 * Puerto de solo lectura hacia los módulos event/ticket, calcado del patrón de
 * ArtistTourHistoryAdapter (artist.infrastructure.adapters.tourhistory): sin persistencia
 * propia, calcula ingresos en caliente a partir de tickets no cancelados x precio efectivo.
 */
public interface EventRevenueProviderPort {
    EventRevenueSnapshot getRevenueSnapshot(UUID eventId);

    /** @throws EventNotFoundException si el evento no existe. */
    PromoterId getPromoterIdForEvent(UUID eventId);
}
