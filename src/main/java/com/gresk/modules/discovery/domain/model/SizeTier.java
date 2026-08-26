package com.gresk.modules.discovery.domain.model;

/**
 * Clasificación del tamaño de un artista según la popularidad de Spotify
 * (0-100, aproximación más cercana a oyentes mensuales que expone la API
 * gratuita de Spotify). Cuanto más pequeño, más interesante para el
 * descubrimiento — es un filtro positivo, no un obstáculo.
 */
public enum SizeTier {
    NO_SPOTIFY,   // sin vínculo a Spotify: solo catálogo GresK
    MICRO,        // popularity < 15  (~< 1K oyentes/mes)
    SMALL,        // popularity 15-30 (1K-10K)
    EMERGING,     // popularity 30-45 (10K-100K)
    GROWING,      // popularity 45-60 (100K-500K)
    ESTABLISHED;  // popularity > 60

    public static SizeTier fromPopularity(Integer popularity) {
        if (popularity == null) return NO_SPOTIFY;
        if (popularity < 15) return MICRO;
        if (popularity < 30) return SMALL;
        if (popularity < 45) return EMERGING;
        if (popularity < 60) return GROWING;
        return ESTABLISHED;
    }
}
