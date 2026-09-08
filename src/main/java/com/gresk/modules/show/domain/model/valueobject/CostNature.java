package com.gresk.modules.show.domain.model.valueobject;

public enum CostNature {
    /** No varía con la asistencia (alquiler de sala, caché de artista, marketing...). */
    FIXED,
    /** Coste por cada entrada vendida/asistente (comisión de ticketing, catering de staff...). */
    VARIABLE_PER_ATTENDEE
}
