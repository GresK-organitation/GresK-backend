package com.gresk.modules.promoter.infrastructure.web;

import com.gresk.modules.promoter.domain.MusicGenre;
import com.gresk.modules.promoter.domain.model.Promoter;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record PromoterResponse(
        String id,
        String name,
        String email,
        String city,
        String country,
        String description,
        String status,
        Set<String> musicalGenres,
        LocalDateTime createdAt
) {
    public static PromoterResponse from(Promoter promoter) {
        return new PromoterResponse(
                promoter.getId().value().toString(),
                promoter.getName().value(),
                promoter.getEmail().value(),
                promoter.getLocation().city(),
                promoter.getLocation().country(),
                promoter.getDescription() != null ? promoter.getDescription().value() : null,
                promoter.getStatus().name(),
                promoter.getMusicalGenres().stream()
                        .map(MusicGenre::name)
                        .collect(Collectors.toCollection(LinkedHashSet::new)),
                promoter.getCreatedAt()
        );
    }
}
