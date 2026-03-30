package com.gresk.modules.promoter.infrastructure.web;

import jakarta.validation.constraints.Size;

import java.util.Set;

public record UpdatePromoterProfileRequest(
        String name,
        String city,
        String country,
        String address,

        @Size(max = 500)
        String description,
        Set<String> musicalGenres
) {}
