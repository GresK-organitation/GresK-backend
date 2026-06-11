package com.gresk.modules.email.domain.port.out;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.util.Optional;
import java.util.UUID;

/**
 * Consulta mínima al módulo de eventos: título del evento si pertenece al
 * promotor. Sirve a la vez como dato del panel y como verificación de
 * propiedad (un promotor no puede consultar eventos ajenos).
 */
public interface EventInfoPort {
    Optional<String> findEventTitle(UUID eventId, PromoterId promoterId);
}
