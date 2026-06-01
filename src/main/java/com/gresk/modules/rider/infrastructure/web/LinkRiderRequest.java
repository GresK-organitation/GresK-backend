package com.gresk.modules.rider.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

public record LinkRiderRequest(
        @NotBlank String riderId
) {}
