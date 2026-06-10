package com.gresk.modules.email.domain.model;

/** Capa del pipeline híbrido que produjo la clasificación. */
public enum ClassificationSource {
    RULES,
    OLLAMA,
    CLAUDE
}
