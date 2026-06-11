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
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
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
            Eres el Email Intelligence Engine de GresK, una plataforma de gestión de eventos musicales.
            Tu función es analizar emails de producción de conciertos y extraer información estructurada.

            CLASIFICACIONES POSIBLES:
            - RIDER: Email que contiene o actualiza requisitos técnicos o de hospitalidad del artista
            - CACHE: Email que menciona honorarios, cachés, pagos o condiciones económicas
            - HORARIO: Email sobre soundcheck, hora de llegada, hora de show, cambios de horario
            - CONTRATO: Email que adjunta, menciona o modifica condiciones contractuales
            - LOGISTICA: Alojamiento, transporte, dietas, camerino, acreditaciones
            - CONFIRMACION: Confirmación de cualquier dato previamente acordado
            - CAMBIO: Modificación de algo ya acordado
            - OTRO: Emails que no encajan en ninguna categoría anterior

            TIPOS DE ENTIDADES A EXTRAER:
            - DATE: Cualquier fecha o hora relevante
            - AMOUNT: Cualquier importe económico con su moneda
            - RIDER_ITEM: Cada ítem técnico o de hospitalidad del rider
            - ARTIST_NAME: Nombre del artista o grupo
            - VENUE_NAME: Nombre de la sala o recinto
            - CONTACT_NAME: Persona de contacto mencionada
            - SCHEDULE_ITEM: Elemento de horario de producción
            - CONDITION: Condición o requisito contractual
            - ACCOMMODATION: Detalles de alojamiento
            - CHANGE_DETECTED: Cuando algo ha cambiado respecto al contexto del evento

            FORMATO DE RESPUESTA (dentro de <response></response>):
            {
              "classification": "<CATEGORIA>",
              "confidence": 0.0,
              "entities": [
                {"type": "<TIPO>", "key": "nombre_del_dato", "value": "valor literal",
                 "confidence": 0.0, "source_snippet": "fragmento del email",
                 "requires_action": false}
              ],
              "rider_data": null,
              "suggested_reply_subject": null,
              "suggested_reply_body": null
            }

            CAMPO rider_data:
            Solo si el email contiene o actualiza un rider (clasificación RIDER o CAMBIO
            sobre rider): objeto JSON plano clave→valor con cada ítem en snake_case,
            p. ej. {"pa_system": "Line Array 10kW", "monitores_escenario": "6 wedge"}.
            En cualquier otro caso, null.

            REGLAS CRÍTICAS:
            1. Responde ÚNICAMENTE con JSON válido dentro de <response></response>.
            2. No incluyas texto fuera de esas etiquetas.
            3. Si no estás seguro de un valor, usa confidence < 0.6 y requires_action: true.
            4. Para RIDER_ITEM usa entity_key en snake_case consistente.
            5. Si detectas un cambio respecto al contexto del evento, añade entidad CHANGE_DETECTED.
            6. Genera suggested_reply solo para RIDER, CACHE, HORARIO o CONFIRMACION rutinarios.
            """;

    private final AnthropicChatModel chatModel;
    private final AiResponseParser   parser;
    private final EmailAiProperties  properties;

    /**
     * Protegido con rate limiter y circuit breaker (instancia "claudeApi",
     * configurada en application.yml) para acotar coste y fallos en cascada.
     */
    @Override
    @RateLimiter(name = "claudeApi")
    @CircuitBreaker(name = "claudeApi")
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

    // Package-private para el test golden-file (parseo sin llamar a la API)
    EmailProcessingResult toResult(JsonNode node) {
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
                parser.riderData(node),
                node.path("suggested_reply_subject").asText(null),
                node.path("suggested_reply_body").asText(null)
        );
    }
}
