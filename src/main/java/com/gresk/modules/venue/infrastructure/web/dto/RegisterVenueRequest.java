package com.gresk.modules.venue.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterVenueRequest(
        @NotBlank String name,
        @NotBlank String street,
        @NotBlank String city,
        @NotBlank String country
) {}
