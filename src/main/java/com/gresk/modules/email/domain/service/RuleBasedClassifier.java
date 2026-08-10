package com.gresk.modules.email.domain.service;

import com.gresk.modules.email.domain.model.ClassificationResult;
import com.gresk.modules.email.domain.model.EmailClassification;
import com.gresk.modules.email.domain.model.EmailMessage;
import com.gresk.modules.email.domain.model.EventContext;
import org.springframework.stereotype.Component;

/**
 * Capa 1 del pipeline híbrido: señales deterministas, coste 0.
 * Si ninguna regla alcanza confianza suficiente devuelve un resultado
 * neutro y el pipeline delega en la capa 2 (Ollama).
 */
@Component
public class RuleBasedClassifier {

    public ClassificationResult classify(EmailMessage msg, EventContext ctx) {

        // Hilo ya vinculado a un evento conocido → continuación de conversación
        if (ctx != null && ctx.hasActiveThread(msg.getExternalThreadId())) {
            return ClassificationResult.of(EmailClassification.CONTINUATION, 1.0);
        }

        String subject = lower(msg.getSubject());
        String body    = lower(msg.getBodyText());

        // Patrones de rider
        if (matches(subject, body, "rider", "backline", "pa system", "monitor", "soundcheck")) {
            return ClassificationResult.of(EmailClassification.RIDER, 0.92);
        }
        // Patrones de caché
        if (matches(subject, body, "caché", "cache", "honorario", "pago", "transferencia")) {
            return ClassificationResult.of(EmailClassification.CACHE, 0.92);
        }
        // Patrones de horario
        if (matches(subject, body, "horario", "llegada", "prueba de sonido", "set time")) {
            return ClassificationResult.of(EmailClassification.HORARIO, 0.90);
        }
        // Patrones de contrato
        if (matches(subject, body, "contrato", "firma", "cláusula", "clausula")) {
            return ClassificationResult.of(EmailClassification.CONTRATO, 0.90);
        }

        return ClassificationResult.lowConfidence(); // pasa a capa 2
    }

    private boolean matches(String subject, String body, String... patterns) {
        for (String pattern : patterns) {
            if (subject.contains(pattern) || body.contains(pattern)) {
                return true;
            }
        }
        return false;
    }

    private String lower(String text) {
        return text != null ? text.toLowerCase() : "";
    }
}
