package com.gresk.modules.artist.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

public record AddBandMemberRequest(
        @NotBlank String name,
        @NotBlank String roleInBand
) {}
