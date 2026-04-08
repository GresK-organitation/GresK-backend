package com.gresk.modules.promoter.application.dto;

import java.util.Set;

public record PromoterDashboardDTO(
        String name,
        String logoUrl,
        String description,
        String street,
        String city,
        String country,
        Set<String> musicalGenres,
        double averageRating
) {}
