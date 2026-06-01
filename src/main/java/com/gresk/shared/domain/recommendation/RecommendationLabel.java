package com.gresk.shared.domain.recommendation;

// Priority: PLAYING_THIS_WEEK > NEW_RELEASE > TRENDING_THIS_WEEK > NUEVO_ARTISTA
//           > ACORDE_A_TUS_GUSTOS (nivel 2) > ESCENA_UNDERGROUND (nivel 3)
public enum RecommendationLabel {
    ESCENA_UNDERGROUND,
    ACORDE_A_TUS_GUSTOS,
    NUEVO_ARTISTA,
    TRENDING_THIS_WEEK,
    NEW_RELEASE,
    PLAYING_THIS_WEEK
}
