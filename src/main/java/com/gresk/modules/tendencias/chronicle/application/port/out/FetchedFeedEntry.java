package com.gresk.modules.tendencias.chronicle.application.port.out;

import java.time.Instant;

/** DTO crudo devuelto por el adapter de fetch — sin lógica de dominio todavía. */
public record FetchedFeedEntry(String guid, String title, String rawSummary, String link, Instant publishedAt) {
}
