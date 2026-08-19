package com.gresk.modules.musicdna.domain.port.out;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Señales agregadas de un usuario, ya resueltas por infraestructura, sobre
 * las que operan las 6 fórmulas puras de {@code MusicDnaFormulas}. El
 * dominio nunca ve entidades JPA de otros módulos — solo este DTO.
 */
public record MusicDnaSignals(
        long reviewCount,
        long journalCount,
        long writtenReviewCount,
        long writtenJournalCount,
        long totalReviewLikes,
        long totalCustomCriteriaUsage,
        int distinctGenresMin2,
        long reviewLocalMatches,
        long journalLocalMatches,
        LocalDate oldestDocumentedDate,
        Instant userCreatedAt
) {
    public long totalDocumented() {
        return reviewCount + journalCount;
    }
}
