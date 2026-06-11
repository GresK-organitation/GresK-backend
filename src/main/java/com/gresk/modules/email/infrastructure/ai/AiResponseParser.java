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
        return objectMapper.readTree(stripMarkdownFences(extractResponseTag(rawResponse)));
    }

    /** Claude responde con el JSON envuelto en <response></response>. */
    private String extractResponseTag(String text) {
        int start = text.indexOf("<response>");
        int end   = text.lastIndexOf("</response>");
        if (start >= 0 && end > start) {
            return text.substring(start + "<response>".length(), end).trim();
        }
        return text;
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

    /** Campo rider_data: objeto plano clave→valor con los ítems del rider. */
    public java.util.Map<String, Object> riderData(JsonNode node) {
        JsonNode riderData = node.path("rider_data");
        if (!riderData.isObject() || riderData.isEmpty()) {
            return null;
        }
        return objectMapper.convertValue(riderData,
                new com.fasterxml.jackson.core.type.TypeReference<>() {});
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
