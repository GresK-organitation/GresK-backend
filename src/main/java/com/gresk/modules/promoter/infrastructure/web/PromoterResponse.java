package com.gresk.modules.promoter.infrastructure.web;

import com.gresk.modules.promoter.domain.model.Promoter;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record PromoterResponse(
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
) {
    public static PromoterResponse from(Promoter promoter, String logoUrl) {
        return new PromoterResponse(
                promoter.getId().value().toString(),
                promoter.getName().value(),
                promoter.getEmail().value(),
                promoter.getAddress().street(),
                promoter.getAddress().city().value(),
                promoter.getAddress().country(),
                logoUrl,
                promoter.getDescription() != null ? promoter.getDescription().value() : null,
                promoter.getStatus().name(),
                promoter.getMusicalGenres().stream()
                        .map(Enum::name)
                        .collect(Collectors.toCollection(LinkedHashSet::new)),
                promoter.getCreatedAt().toString()
        );
    }
}
