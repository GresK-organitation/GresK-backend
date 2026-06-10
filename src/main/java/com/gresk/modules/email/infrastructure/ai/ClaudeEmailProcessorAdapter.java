package com.gresk.modules.email.infrastructure.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.gresk.modules.email.domain.model.ClassificationResult;
import com.gresk.modules.email.domain.model.ClassificationSource;
import com.gresk.modules.email.domain.model.EmailMessage;
import com.gresk.modules.email.domain.model.EmailProcessingResult;
import com.gresk.modules.email.domain.model.EmailProcessingResult.ExtractedEntity;
import com.gresk.modules.email.domain.model.EventContext;
import com.gresk.modules.email.domain.model.ExtractedEntityType;
import com.gresk.modules.email.domain.port.out.AiEmailProcessorPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Capa 3 del pipeline: procesamiento completo con Claude. Además de
 * clasificar, extrae entidades estructuradas y puede sugerir un borrador
 * de respuesta. Solo recibe los emails que las capas baratas no resolvieron.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ClaudeEmailProcessorAdapter implements AiEmailProcessorPort {

    private static final String SYSTEM_PROMPT = """
            Eres el asistente de email de una promotora de conciertos. Analiza el email y devuelve SOLO JSON válido con esta estructura exacta:
            {
              "classification": "RIDER|CACHE|HORARIO|CONTRATO|LOGISTICA|CONFIRMACION|CAMBIO|OTRO",
              "confidence": 0.0,
              "entities": [
                {"type": "DATE|AMOUNT|RIDER_ITEM|ARTIST_NAME|VENUE_NAME|CONTACT_NAME|SCHEDULE_ITEM|CONDITION|ACCOMMODATION|TRANSPORT|CHANGE_DETECTED",
                 "key": "nombre corto del dato", "value": "valor literal", "confidence": 0.0,
                 "source_snippet": "fragmento del email donde aparece", "requires_action": false}
              ],
              "suggested_reply_subject": null,
              "suggested_reply_body": null
            }
            Extrae solo entidades presentes en el texto. Sugiere respuesta únicamente si el email pide algo concreto que la promotora deba contestar; escríbela en el idioma del email, profesional y breve.
            """;

    private final AnthropicChatModel chatModel;
    private final AiResponseParser   parser;
    private final EmailAiProperties  properties;

    @Override
    public EmailProcessingResult process(EmailMessage message, EventContext context) {
        try {
            Prompt prompt = new Prompt(
                    List.of(new SystemMessage(SYSTEM_PROMPT),
                            new UserMessage(EmailPromptFormatter.format(message))),
                    AnthropicChatOptions.builder()
                            .model(properties.claudeModel())
                            .maxTokens(2048)
                            .temperature(0.0)
                            .build()
            );
            String response = chatModel.call(prompt).getResult().getOutput().getText();
            return toResult(parser.parse(response));

        } catch (Exception e) {
            // La capa 3 es la última: el fallo debe propagarse para que el
            // email quede FAILED y se reintente
            throw new IllegalStateException(
                    "Claude processing failed for email " + message.getId(), e);
        }
    }

    private EmailProcessingResult toResult(JsonNode node) {
        ClassificationResult classification = new ClassificationResult(
                parser.classification(node),
                parser.confidence(node),
                ClassificationSource.CLAUDE
        );

        List<ExtractedEntity> entities = new ArrayList<>();
        for (JsonNode entity : node.path("entities")) {
            ExtractedEntityType type = parser.entityType(entity.path("type").asText(""));
            if (type == null) continue;
            entities.add(new ExtractedEntity(
                    type,
                    entity.path("key").asText(null),
                    entity.path("value").asText(""),
                    null,
                    Math.clamp(entity.path("confidence").asDouble(0.0), 0.0, 1.0),
                    entity.path("source_snippet").asText(null),
                    entity.path("requires_action").asBoolean(false)
            ));
        }

        return new EmailProcessingResult(
                classification,
                entities,
                node.path("suggested_reply_subject").asText(null),
                node.path("suggested_reply_body").asText(null)
        );
    }
}
