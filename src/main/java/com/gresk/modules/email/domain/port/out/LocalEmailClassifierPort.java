package com.gresk.modules.email.domain.port.out;

import com.gresk.modules.email.domain.model.ClassificationResult;
import com.gresk.modules.email.domain.model.EmailMessage;
import com.gresk.modules.email.domain.model.EventContext;

/**
 * Capa 2 del pipeline híbrido: clasificador con modelo local (Ollama).
 * Coste 0; si no alcanza el umbral de confianza se delega en la capa 3.
 */
public interface LocalEmailClassifierPort {
    ClassificationResult classify(EmailMessage message, EventContext context);
}
