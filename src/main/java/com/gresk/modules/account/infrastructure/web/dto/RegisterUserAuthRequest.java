package com.gresk.modules.account.infrastructure.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record RegisterUserAuthRequest(
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotBlank String name,
        String description,
        @NotBlank String city,
        Set<String> musicGenres
) {}
