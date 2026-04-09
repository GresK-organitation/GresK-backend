package com.gresk.modules.account.infrastructure.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record RegisterPromoterAuthRequest(
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotBlank String name,
        @NotBlank String street,
        @NotBlank String city,
        @NotBlank String country,
        String description,
        Set<String> musicalGenres,
        String phone,
        String website
) {}
