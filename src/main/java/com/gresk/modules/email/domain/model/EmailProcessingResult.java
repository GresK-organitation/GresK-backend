package com.gresk.modules.email.domain.model;

import java.util.List;
import java.util.Objects;

/**
 * Resultado completo del procesamiento IA de un correo: clasificación,
 * entidades extraídas y, opcionalmente, un borrador de respuesta sugerido.
 * Las capas baratas del pipeline (reglas, Ollama) solo aportan clasificación;
 * Claude puede aportar el resto.
 */
public record EmailProcessingResult(ClassificationResult classification,
                                    List<ExtractedEntity> entities,
                                    String suggestedReplySubject,
                                    String suggestedReplyBody) {

    public EmailProcessingResult {
        Objects.requireNonNull(classification, "classification must not be null");
        entities = entities != null ? List.copyOf(entities) : List.of();
    }

    public static EmailProcessingResult classificationOnly(ClassificationResult classification) {
        return new EmailProcessingResult(classification, List.of(), null, null);
    }

    public boolean hasSuggestedReply() {
        return suggestedReplyBody != null && !suggestedReplyBody.isBlank();
    }

    public record ExtractedEntity(ExtractedEntityType type,
                                  String key,
                                  String value,
                                  String normalizedValueJson,
                                  double confidence,
                                  String sourceSnippet,
                                  boolean requiresAction) {}
}
