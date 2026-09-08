package com.gresk.modules.logistics.domain.model.valueobject;

import com.gresk.modules.logistics.domain.model.NeedType;

/**
 * Necesidad individual de un miembro del travel party (dieta, movilidad, médica...).
 * Inspirado en el patrón SSR (Special Service Request) del PNR de Sabre: metadatos
 * flexibles por viajero en vez de campos fijos en la entidad persona.
 */
public record IndividualNeed(NeedType type, String description) {

    public IndividualNeed {
        if (type == null) throw new IllegalArgumentException("IndividualNeed type must not be null");
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("IndividualNeed description must not be blank");
        }
    }
}
