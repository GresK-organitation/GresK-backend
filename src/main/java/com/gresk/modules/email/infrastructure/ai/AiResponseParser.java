package com.gresk.modules.email.infrastructure.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gresk.modules.email.domain.model.EmailClassification;
import com.gresk.modules.email.domain.model.ExtractedEntityType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** Parseo defensivo de las respuestas JSON de los modelos (Ollama / Claude). */
@Slf4j
@Component
@RequiredArgsConstructor
public class AiResponseParser {

    private final ObjectMapper objectMapper;

    public JsonNode parse(String rawResponse) throws Exception {
        return objectMapper.readTree(stripMarkdownFences(rawResponse));
    }

    public EmailClassification classification(JsonNode node) {
        String value = node.path("classification").asText("");
        try {
            return EmailClassification.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Unknown classification '{}' from model, defaulting to OTRO", value);
            return EmailClassification.OTRO;
        }
    }

    public double confidence(JsonNode node) {
        double confidence = node.path("confidence").asDouble(0.0);
        return Math.clamp(confidence, 0.0, 1.0);
    }

    public ExtractedEntityType entityType(String value) {
        try {
            return ExtractedEntityType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /** Los modelos a veces envuelven el JSON en bloques ```json ... ```. */
    private String stripMarkdownFences(String text) {
        String cleaned = text.trim();
        if (cleaned.startsWith("```")) {
            int firstNewline = cleaned.indexOf('\n');
            int lastFence    = cleaned.lastIndexOf("```");
            if (firstNewline >= 0 && lastFence > firstNewline) {
                cleaned = cleaned.substring(firstNewline + 1, lastFence).trim();
            }
        }
        return cleaned;
    }
}
