package com.gresk.modules.rider.domain.model.valueobject;

import com.gresk.modules.rider.domain.model.BacklineCategory;

public record BacklineItem(
        BacklineCategory category,
        String description,
        String brand,
        String model,
        boolean required
) {
    public BacklineItem {
        if (category == null) throw new IllegalArgumentException("BacklineItem category cannot be null");
        if (description == null || description.isBlank()) throw new IllegalArgumentException("BacklineItem description cannot be blank");
    }
}
