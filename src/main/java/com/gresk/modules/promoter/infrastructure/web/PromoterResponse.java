package com.gresk.modules.promoter.infrastructure.web;

import com.gresk.modules.promoter.application.dto.PromoterProfileDTO;

import java.util.Set;

public record PromoterResponse(
        String id,
        String name,
        String email,
        String street,
        String city,
        String country,
        String logoUrl,
        String description,
        Set<String> musicalGenres,
        String createdAt
) {
    public static PromoterResponse from(PromoterProfileDTO dto) {
        return new PromoterResponse(
                dto.id(),
                dto.name(),
                dto.email(),
                dto.street(),
                dto.city(),
                dto.country(),
                dto.logoUrl(),
                dto.description(),
                dto.musicalGenres(),
                dto.createdAt()
        );
    }
}
