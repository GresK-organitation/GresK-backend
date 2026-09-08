package com.gresk.modules.venue.application.command;

import com.gresk.modules.venue.domain.model.valueobject.CapacityLayout;

public record AddCapacityConfigurationCommand(
        String venueId,
        String promoterId,
        String code,
        String label,
        CapacityLayout layout,
        int maxCapacity
) {
}
