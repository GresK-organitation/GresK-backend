package com.gresk.modules.email.domain.model;

import java.util.Objects;

/**
 * Resultado de una capa del pipeline de clasificación.
 * La confianza se expresa en [0.0, 1.0].
 */
public record ClassificationResult(EmailClassification classification,
                                   double confidence,
                                   ClassificationSource source) {

    public ClassificationResult {
        Objects.requireNonNull(classification, "classification must not be null");
        if (confidence < 0.0 || confidence > 1.0) {
            throw new IllegalArgumentException("confidence must be in [0.0, 1.0]: " + confidence);
        }
    }

    public static ClassificationResult of(EmailClassification classification, double confidence) {
        return new ClassificationResult(classification, confidence, null);
    }

    /** Resultado neutro: la capa no resuelve y delega en la siguiente. */
    public static ClassificationResult lowConfidence() {
        return new ClassificationResult(EmailClassification.OTRO, 0.0, null);
    }

    public ClassificationResult withSource(ClassificationSource source) {
        return new ClassificationResult(classification, confidence, source);
    }

    public boolean meetsThreshold(double threshold) {
        return confidence >= threshold;
    }
}
