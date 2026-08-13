package com.gresk.modules.journal.infrastructure.web.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RatingCriterionRequest(
        @NotBlank(message = "label must not be blank")
        String label,

        @NotNull(message = "value is required")
        @Min(value = 1, message = "value must be at least 1")
        @Max(value = 5, message = "value must be at most 5")
        Integer value
) {}
