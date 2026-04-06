package com.gresk.modules.user.infrastructure.in.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record UpdateUserProfileRequest(
        @NotBlank String name,
        String description,
        @NotBlank String city,
        @NotEmpty Set<String> musicGenres
) {}