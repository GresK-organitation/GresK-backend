package com.gresk.modules.identity.infrastructure.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record RegisterPromoterAuthRequest(
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotBlank String name,
        @NotBlank String city,
        @NotBlank String country,
        String address,
        String description,
        List<String> musicalGenres
) {}
