package com.gresk.modules.email.domain.service;

import com.gresk.modules.email.domain.exception.EmailClassificationException;
import com.gresk.modules.email.domain.model.*;
import com.gresk.modules.email.domain.port.out.AiEmailProcessorPort;
import com.gresk.modules.email.domain.port.out.LocalEmailClassifierPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailClassificationPipelineTest {

    private static final double THRESHOLD = 0.85;

    @Mock private RuleBasedClassifier      ruleClassifier;
    @Mock private LocalEmailClassifierPort localClassifier;
    @Mock private AiEmailProcessorPort     aiProcessor;

    private EmailClassificationPipeline pipeline;
    private EmailMessage email;

    @BeforeEach
    void setUp() {
        pipeline = new EmailClassificationPipeline(
                ruleClassifier, localClassifier, aiProcessor, THRESHOLD);
        email = EmailMessage.receive(
                PromoterId.of(UUID.randomUUID()), "msg-1", "thread-1",
                "promotor@sala.com", null, List.of(), "Asunto", "Cuerpo",
                null, null, Instant.now());
    }

    @Test
    void siLasReglasSuperanElUmbral_ollamaNoSeLlama() {
        when(ruleClassifier.classify(any(), any()))
                .thenReturn(ClassificationResult.of(EmailClassification.HORARIO, 0.90));

        EmailProcessingResult result = pipeline.process(email, EventContext.empty());

        assertEquals(EmailClassification.HORARIO, result.classification().classification());
        assertEquals(ClassificationSource.RULES, result.classification().source());
        verify(localClassifier, never()).classify(any(), any());
        verify(aiProcessor, never()).process(any(), any());
    }

    @Test
    void unRiderResueltoPorReglasEscalaAClaudeParaExtraccion_sinPasarPorOllama() {
        when(ruleClassifier.classify(any(), any()))
                .thenReturn(ClassificationResult.of(EmailClassification.RIDER, 0.92));
        when(aiProcessor.process(any(), any())).thenReturn(
                EmailProcessingResult.classificationOnly(
                        new ClassificationResult(EmailClassification.RIDER, 0.95,
                                ClassificationSource.CLAUDE)));

        EmailProcessingResult result = pipeline.process(email, EventContext.empty());

        assertEquals(ClassificationSource.CLAUDE, result.classification().source());
        verify(localClassifier, never()).classify(any(), any());
        verify(aiProcessor).process(any(), any());
    }

    @Test
    void siLasReglasNoResuelven_yOllamaSupera_claudeNoSeLlama() {
        when(ruleClassifier.classify(any(), any()))
                .thenReturn(ClassificationResult.lowConfidence());
        when(localClassifier.classify(any(), any()))
                .thenReturn(ClassificationResult.of(EmailClassification.HORARIO, 0.90));

        EmailProcessingResult result = pipeline.process(email, EventContext.empty());

        assertEquals(EmailClassification.HORARIO, result.classification().classification());
        assertEquals(ClassificationSource.OLLAMA, result.classification().source());
        verify(aiProcessor, never()).process(any(), any());
    }

    @Test
    void siNingunaCapaBarataResuelve_seDelegaEnClaude() {
        when(ruleClassifier.classify(any(), any()))
                .thenReturn(ClassificationResult.lowConfidence());
        when(localClassifier.classify(any(), any()))
                .thenReturn(ClassificationResult.of(EmailClassification.OTRO, 0.40));
        EmailProcessingResult claudeResult = EmailProcessingResult.classificationOnly(
                new ClassificationResult(EmailClassification.CAMBIO, 0.95, ClassificationSource.CLAUDE));
        when(aiProcessor.process(any(), any())).thenReturn(claudeResult);

        EmailProcessingResult result = pipeline.process(email, EventContext.empty());

        assertEquals(EmailClassification.CAMBIO, result.classification().classification());
        assertEquals(ClassificationSource.CLAUDE, result.classification().source());
    }

    @Test
    void siOllamaFalla_seDegradaAClaudeSinPropagar() {
        when(ruleClassifier.classify(any(), any()))
                .thenReturn(ClassificationResult.lowConfidence());
        when(localClassifier.classify(any(), any()))
                .thenThrow(new IllegalStateException("Ollama no disponible"));
        when(aiProcessor.process(any(), any())).thenReturn(
                EmailProcessingResult.classificationOnly(
                        new ClassificationResult(EmailClassification.OTRO, 0.70, ClassificationSource.CLAUDE)));

        EmailProcessingResult result = pipeline.process(email, EventContext.empty());

        assertEquals(ClassificationSource.CLAUDE, result.classification().source());
    }

    @Test
    void siClaudeTambienFalla_sePropagaParaQueElEmailQuedeReintentable() {
        when(ruleClassifier.classify(any(), any()))
                .thenReturn(ClassificationResult.lowConfidence());
        when(localClassifier.classify(any(), any()))
                .thenReturn(ClassificationResult.lowConfidence());
        when(aiProcessor.process(any(), any()))
                .thenThrow(new IllegalStateException("API caída"));

        assertThrows(EmailClassificationException.class,
                () -> pipeline.process(email, EventContext.empty()));
    }
}
