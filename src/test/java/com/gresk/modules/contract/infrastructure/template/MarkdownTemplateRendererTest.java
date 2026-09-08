package com.gresk.modules.contract.infrastructure.template;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarkdownTemplateRendererTest {

    private final MarkdownTemplateRenderer renderer = new MarkdownTemplateRenderer();

    @Test
    void interpolaVariablesAnidadasPorPath() {
        Map<String, Object> vars = Map.of(
                "partyA", Map.of("name", "Barcelona Live"),
                "financial", Map.of("feeAmount", "1500.00"));

        String result = renderer.interpolate("Caché: {{financial.feeAmount}} para {{partyA.name}}", vars);

        assertEquals("Caché: 1500.00 para Barcelona Live", result);
    }

    @Test
    void variableAusenteSeSustituyePorVacio() {
        String result = renderer.interpolate("Valor: {{no.existe}}", Map.of());
        assertEquals("Valor: ", result);
    }

    @Test
    void renderToPdfProduceUnPdfValido() {
        byte[] pdf = renderer.renderToPdf("# Contrato\n\nHola {{partyA.name}}", Map.of("partyA", Map.of("name", "Test")));
        assertTrue(pdf.length > 0);
        String header = new String(pdf, 0, Math.min(5, pdf.length), StandardCharsets.US_ASCII);
        assertEquals("%PDF-", header);
    }
}
