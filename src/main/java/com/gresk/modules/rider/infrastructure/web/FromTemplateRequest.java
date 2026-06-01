package com.gresk.modules.rider.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

public record FromTemplateRequest(
        @NotBlank String template,
        @NotBlank String artistId,
        @NotBlank String name
) {}
