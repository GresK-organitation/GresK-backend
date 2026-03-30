package com.gresk.modules.promoter.infrastructure.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RegisterPromoterRequest(
        @NotBlank @Email
        String email,

        @NotBlank @Size(min = 8)
        String password,

        @NotBlank @Size(min = 8, max = 100)
        String name,

        @NotBlank
        String city,

        @NotBlank
        String country,

        String address,

        @Size (max = 500)
        String description,

        List<String> musicalGenres
) {}
