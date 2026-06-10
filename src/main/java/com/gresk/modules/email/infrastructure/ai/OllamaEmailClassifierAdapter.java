package com.gresk.modules.email.infrastructure.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.gresk.modules.email.domain.model.ClassificationResult;
import com.gresk.modules.email.domain.model.EmailMessage;
import com.gresk.modules.email.domain.model.EventContext;
import com.gresk.modules.email.domain.port.out.LocalEmailClassifierPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Capa 2 del pipeline: clasificación con modelo local vía Ollama (coste 0).
 * Si Ollama no está disponible o la respuesta no es parseable, devuelve
 * baja confianza y el pipeline delega en la capa 3.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OllamaEmailClassifierAdapter implements LocalEmailClassifierPort {

    private static final String SYSTEM_PROMPT = """
            Eres un clasificador de emails para una promotora de conciertos.
            Clasifica el email en UNA de estas categorías:
            RIDER (requisitos técnicos/hospitality), CACHE (honorarios y pagos),
            HORARIO (horarios y llegadas), CONTRATO (contratos y firmas),
            LOGISTICA (transporte y alojamiento), CONFIRMACION (confirmaciones),
            CAMBIO (cambios sobre lo acordado), OTRO (cualquier otra cosa).
            Responde SOLO con JSON válido, sin texto adicional:
            {"classification": "<CATEGORIA>", "confidence": <0.0-1.0>}
            """;

    private final OllamaChatModel  chatModel;
    private final AiResponseParser parser;

    @Override
    public ClassificationResult classify(EmailMessage message, EventContext context) {
        try {
            Prompt prompt = new Prompt(List.of(
                    new SystemMessage(SYSTEM_PROMPT),
                    new UserMessage(EmailPromptFormatter.format(message))
            ));
            String response = chatModel.call(prompt).getResult().getOutput().getText();

            JsonNode node = parser.parse(response);
            return ClassificationResult.of(parser.classification(node), parser.confidence(node));

        } catch (Exception e) {
            log.warn("Ollama classification failed for email {}: {}", message.getId(), e.getMessage());
            return ClassificationResult.lowConfidence();
        }
    }
}
