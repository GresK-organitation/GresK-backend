package com.gresk.modules.venue.infrastructure.web.dto;

import com.gresk.modules.venue.domain.model.valueobject.CapacityLayout;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddCapacityConfigurationRequest(
        @NotBlank String code,
        @NotBlank String label,
        @NotNull  CapacityLayout layout,
        @Min(1)   int maxCapacity
) {}
