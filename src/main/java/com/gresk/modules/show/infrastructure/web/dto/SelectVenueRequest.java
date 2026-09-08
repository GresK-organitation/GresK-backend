package com.gresk.modules.show.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public record SelectVenueRequest(@NotBlank String venueId, @NotBlank String capacityConfigCode) {}
