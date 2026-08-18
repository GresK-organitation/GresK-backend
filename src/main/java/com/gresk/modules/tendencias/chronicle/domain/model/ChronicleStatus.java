package com.gresk.modules.tendencias.chronicle.domain.model;

/**
 * PUBLISHED se asigna automáticamente al ingerir (la fuente ya fue vetada por
 * un ADMIN a nivel de FeedSource). HIDDEN es la vía de escape manual para
 * ocultar un artículo puntual problemático sin rechazar toda la fuente.
 */
public enum ChronicleStatus {
    PUBLISHED,
    HIDDEN
}
