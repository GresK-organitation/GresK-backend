package com.gresk.modules.rider.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

public record CreateRiderRequest(
        @NotBlank String artistId,
        @NotBlank String name
) {}
