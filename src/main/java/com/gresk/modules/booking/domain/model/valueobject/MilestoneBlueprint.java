package com.gresk.modules.booking.domain.model.valueobject;

import com.gresk.modules.booking.domain.model.MilestoneType;

/**
 * Plantilla usada por el motor de hitos ({@code MilestoneEngine}) para generar
 * {@link Milestone} concretos al crear o reprogramar un {@code Booking}.
 */
public record MilestoneBlueprint(MilestoneType type, String title, MilestoneOffset offset) {

    public MilestoneBlueprint {
        if (type == null) {
            throw new IllegalArgumentException("MilestoneBlueprint type must not be null");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("MilestoneBlueprint title must not be blank");
        }
        if (offset == null) {
            throw new IllegalArgumentException("MilestoneBlueprint offset must not be null");
        }
    }
}
