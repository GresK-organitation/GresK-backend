package com.gresk.modules.email.domain.port.out;

import com.gresk.modules.email.domain.model.EmailMessage;
import com.gresk.modules.email.domain.model.EmailProcessingResult;
import com.gresk.modules.email.domain.model.EventContext;

/**
 * Capa 3 del pipeline híbrido: procesador IA completo (Claude).
 * Además de clasificar, puede extraer entidades y sugerir un borrador
 * de respuesta. Solo se invoca cuando las capas baratas no resuelven.
 */
public interface AiEmailProcessorPort {
    EmailProcessingResult process(EmailMessage message, EventContext context);
}
