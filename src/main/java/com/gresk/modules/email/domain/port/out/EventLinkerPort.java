package com.gresk.modules.email.domain.port.out;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.util.Optional;
import java.util.UUID;

/**
 * Resuelve a qué evento pertenece un correo entrante a partir de señales
 * deterministas (p. ej. otro correo del mismo hilo ya vinculado).
 */
public interface EventLinkerPort {
    Optional<UUID> findEventForThread(PromoterId promoterId, String threadIdExternal);
}
