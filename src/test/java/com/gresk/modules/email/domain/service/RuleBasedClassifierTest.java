package com.gresk.modules.email.domain.service;

import com.gresk.modules.email.domain.model.ClassificationResult;
import com.gresk.modules.email.domain.model.EmailClassification;
import com.gresk.modules.email.domain.model.EmailMessage;
import com.gresk.modules.email.domain.model.EventContext;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuleBasedClassifierTest {

    private final RuleBasedClassifier classifier = new RuleBasedClassifier();

    @Test
    void emailDeRider_seClasificaComoRiderConAltaConfianza() {
        EmailMessage email = email("Rider técnico actualizado",
                "Os adjunto el backline que necesitamos y la configuración de monitores.");

        ClassificationResult result = classifier.classify(email, EventContext.empty());

        assertEquals(EmailClassification.RIDER, result.classification());
        assertTrue(result.confidence() >= 0.85);
    }

    @Test
    void emailDeCache_seClasificaComoCacheConAltaConfianza() {
        EmailMessage email = email("Condiciones económicas",
                "Os confirmo que el pago del honorario se hará por transferencia.");

        ClassificationResult result = classifier.classify(email, EventContext.empty());

        assertEquals(EmailClassification.CACHE, result.classification());
        assertTrue(result.confidence() >= 0.85);
    }

    @Test
    void emailAmbiguo_devuelveBajaConfianzaParaDelegarEnLaSiguienteCapa() {
        EmailMessage email = email("Hola",
                "¿Qué tal todo? Nos vemos pronto por Madrid.");

        ClassificationResult result = classifier.classify(email, EventContext.empty());

        assertEquals(0.0, result.confidence());
    }

    @Test
    void hiloYaVinculadoAEvento_seClasificaComoContinuacionConConfianzaTotal() {
        EmailMessage email = email("Cualquier asunto", "Cualquier cuerpo.");
        EventContext ctx = EventContext.forThread(UUID.randomUUID(), "thread-1");

        ClassificationResult result = classifier.classify(email, ctx);

        assertEquals(EmailClassification.CONTINUATION, result.classification());
        assertEquals(1.0, result.confidence());
    }

    private EmailMessage email(String subject, String body) {
        return EmailMessage.receive(
                PromoterId.of(UUID.randomUUID()),
                "msg-" + UUID.randomUUID(), "thread-1",
                "promotor@sala.com", "Sala Apolo",
                List.of("promotora@gresk.com"),
                subject, body, null, null,
                Instant.now());
    }
}
