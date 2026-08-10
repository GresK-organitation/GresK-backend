package com.gresk.modules.email.domain.service;

import com.gresk.modules.email.domain.exception.EmailClassificationException;
import com.gresk.modules.email.domain.model.ClassificationResult;
import com.gresk.modules.email.domain.model.ClassificationSource;
import com.gresk.modules.email.domain.model.EmailClassification;
import com.gresk.modules.email.domain.model.EmailMessage;
import com.gresk.modules.email.domain.model.EmailProcessingResult;
import com.gresk.modules.email.domain.model.EventContext;
import com.gresk.modules.email.domain.port.out.AiEmailProcessorPort;
import com.gresk.modules.email.domain.port.out.LocalEmailClassifierPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Orquesta las 3 capas del pipeline híbrido de clasificación:
 *   1. Reglas deterministas (coste 0)
 *   2. Modelo local Ollama (coste 0)
 *   3. Claude API (solo si las anteriores no alcanzan el umbral)
 *
 * Las capas 1-2 degradan silenciosamente a la siguiente si fallan o no
 * alcanzan el umbral. Si la capa 3 también falla, se propaga
 * {@link EmailClassificationException} para que el correo quede en estado
 * reintentable (FAILED / DEAD_LETTER).
 *
 * Se instancia vía {@code @Bean} en la configuración del módulo para
 * mantener el dominio libre de dependencias de configuración.
 */
public class EmailClassificationPipeline {

    private static final Logger log = LoggerFactory.getLogger(EmailClassificationPipeline.class);

    private final RuleBasedClassifier      ruleClassifier;
    private final LocalEmailClassifierPort localClassifier;
    private final AiEmailProcessorPort     aiProcessor;
    private final double                   confidenceThreshold;

    public EmailClassificationPipeline(RuleBasedClassifier ruleClassifier,
                                       LocalEmailClassifierPort localClassifier,
                                       AiEmailProcessorPort aiProcessor,
                                       double confidenceThreshold) {
        this.ruleClassifier      = ruleClassifier;
        this.localClassifier     = localClassifier;
        this.aiProcessor         = aiProcessor;
        this.confidenceThreshold = confidenceThreshold;
    }

    /**
     * Clasificaciones que requieren extracción profunda (entidades, rider,
     * borrador): aunque una capa barata las resuelva, se escala a Claude,
     * porque solo la capa 3 extrae datos estructurados. El ahorro de tokens
     * viene del resto de categorías (la gran mayoría del volumen).
     */
    private static final java.util.Set<EmailClassification> NEEDS_DEEP_EXTRACTION =
            java.util.Set.of(EmailClassification.RIDER, EmailClassification.CAMBIO,
                             EmailClassification.CONTINUATION);

    public EmailProcessingResult process(EmailMessage message, EventContext context) {

        // Capa 1 — reglas deterministas (coste 0)
        ClassificationResult rules = ruleClassifier.classify(message, context);
        if (rules.meetsThreshold(confidenceThreshold)) {
            if (NEEDS_DEEP_EXTRACTION.contains(rules.classification())) {
                return processWithAi(message, context);
            }
            return EmailProcessingResult.classificationOnly(
                    rules.withSource(ClassificationSource.RULES));
        }

        // Capa 2 — modelo local Ollama (coste 0)
        ClassificationResult local = classifyLocally(message, context);
        if (local.meetsThreshold(confidenceThreshold)) {
            if (NEEDS_DEEP_EXTRACTION.contains(local.classification())) {
                return processWithAi(message, context);
            }
            return EmailProcessingResult.classificationOnly(
                    local.withSource(ClassificationSource.OLLAMA));
        }

        // Capa 3 — Claude API (las capas baratas no resolvieron)
        return processWithAi(message, context);
    }

    private EmailProcessingResult processWithAi(EmailMessage message, EventContext context) {
        try {
            return aiProcessor.process(message, context);
        } catch (Exception e) {
            throw new EmailClassificationException(
                    "AI processing failed for email " + message.getId(), e);
        }
    }

    private ClassificationResult classifyLocally(EmailMessage message, EventContext context) {
        try {
            return localClassifier.classify(message, context);
        } catch (Exception e) {
            log.warn("Local classifier unavailable for email {}, falling through to AI layer: {}",
                    message.getId(), e.getMessage());
            return ClassificationResult.lowConfidence();
        }
    }
}
