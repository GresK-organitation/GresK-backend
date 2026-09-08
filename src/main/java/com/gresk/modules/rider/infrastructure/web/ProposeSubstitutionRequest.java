package com.gresk.modules.rider.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

public record ProposeSubstitutionRequest(
        @NotBlank String proposedAlternative,
        @NotBlank String proposedBy,
        String notes
) {}
