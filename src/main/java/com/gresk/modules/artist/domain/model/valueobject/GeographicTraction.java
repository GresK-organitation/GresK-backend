package com.gresk.modules.artist.domain.model.valueobject;

import java.util.List;

/**
 * Tracción geográfica agregada de un artista (audiencia por ciudad/país).
 * Puede estar parcial o totalmente vacía según qué integraciones estén
 * disponibles (ni la Spotify Web API pública ni Bandsintown exponen hoy
 * audiencia por ciudad de forma oficial y gratuita).
 */
public record GeographicTraction(List<CityAudience> entries) {

    public GeographicTraction {
        entries = entries != null ? List.copyOf(entries) : List.of();
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public static GeographicTraction empty() {
        return new GeographicTraction(List.of());
    }

    public static GeographicTraction of(List<CityAudience> entries) {
        return new GeographicTraction(entries);
    }
}
