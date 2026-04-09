package com.gresk.modules.promoter.application.dto;

import java.util.Set;

public record PromoterProfileDTO(
        String id,
        String name,
        String email,
        String street,
        String city,
        String country,
        String logoUrl,
        String description,
        String status,
        Set<String> musicalGenres,
        String createdAt
) {}
