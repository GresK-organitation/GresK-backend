package com.gresk.modules.tendencias.chronicle.domain.model;

public record SourceAttribution(String sourceName, String sourceUrl) {

    public SourceAttribution {
        if (sourceName == null || sourceName.isBlank())
            throw new IllegalArgumentException("sourceName must not be blank");
    }
}
