package com.gresk.modules.email.infrastructure.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gresk.modules.email.domain.model.ClassificationSource;
import com.gresk.modules.email.domain.model.EmailClassification;
import com.gresk.modules.email.domain.model.EmailProcessingResult;
import com.gresk.modules.email.domain.model.ExtractedEntityType;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Golden file: valida que la ruta de parseo de la respuesta de Claude
 * (etiquetas <response>, JSON, entidades, borrador) funciona sin llamar
 * a la API real.
 */
class ClaudeEmailProcessorAdapterGoldenTest {

    private final AiResponseParser parser = new AiResponseParser(new ObjectMapper());
    private final ClaudeEmailProcessorAdapter adapter = new ClaudeEmailProcessorAdapter(
            null, parser, new EmailAiProperties(0.85, "claude-haiku-4-5-20251001", 3));

    @Test
    void parseaElGoldenFileDeRiderSinLlamarALaApi() throws Exception {
        String rawResponse = new String(
                Objects.requireNonNull(getClass().getResourceAsStream("/claude/golden_rider_email.json"))
                        .readAllBytes(), StandardCharsets.UTF_8);

        EmailProcessingResult result = adapter.toResult(parser.parse(rawResponse));

        // Clasificación
        assertEquals(EmailClassification.RIDER, result.classification().classification());
        assertEquals(0.93, result.classification().confidence());
        assertEquals(ClassificationSource.CLAUDE, result.classification().source());

        // Entidades extraídas
        assertEquals(4, result.entities().size());

        var riderItem = result.entities().get(0);
        assertEquals(ExtractedEntityType.RIDER_ITEM, riderItem.type());
        assertEquals("monitores_escenario", riderItem.key());
        assertEquals("6 monitores de escenario", riderItem.value());
        assertFalse(riderItem.requiresAction());

        var change = result.entities().get(3);
        assertEquals(ExtractedEntityType.CHANGE_DETECTED, change.type());
        assertTrue(change.requiresAction());
        assertEquals(0.55, change.confidence());

        // Datos de rider para el versionado
        assertTrue(result.hasRiderData());
        assertEquals("6 wedge", result.riderData().get("monitores_escenario"));
        assertEquals("Line Array 10kW", result.riderData().get("pa_system"));

        // Borrador sugerido
        assertTrue(result.hasSuggestedReply());
        assertEquals("Re: Rider actualizado — Arde Bogotá 18/07", result.suggestedReplySubject());
        assertTrue(result.suggestedReplyBody().contains("6 monitores"));
    }

    @Test
    void ignoraEntidadesConTipoDesconocidoSinRomper() throws Exception {
        String response = """
                <response>
                {"classification": "RIDER", "confidence": 0.9,
                 "entities": [{"type": "TIPO_INVENTADO", "value": "x", "confidence": 0.9}]}
                </response>
                """;

        EmailProcessingResult result = adapter.toResult(parser.parse(response));

        assertTrue(result.entities().isEmpty());
        assertEquals(EmailClassification.RIDER, result.classification().classification());
    }
}
