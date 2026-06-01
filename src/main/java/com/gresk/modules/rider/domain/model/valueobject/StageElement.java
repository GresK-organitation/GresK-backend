package com.gresk.modules.rider.domain.model.valueobject;

import com.gresk.modules.rider.domain.model.StageElementType;

import java.util.UUID;

public record StageElement(
        UUID elementId,
        StageElementType type,
        Double xPercent,
        Double yPercent,
        Integer rotationDegrees,
        String label
) {
    public StageElement {
        if (elementId == null) elementId = UUID.randomUUID();
        if (type == null) throw new IllegalArgumentException("StageElement type cannot be null");
        if (xPercent == null || xPercent < 0 || xPercent > 100) throw new IllegalArgumentException("xPercent must be 0–100");
        if (yPercent == null || yPercent < 0 || yPercent > 100) throw new IllegalArgumentException("yPercent must be 0–100");
        if (rotationDegrees == null) rotationDegrees = 0;
    }
}
