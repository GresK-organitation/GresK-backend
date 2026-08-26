package com.gresk.modules.discovery.application.dto;

import com.gresk.modules.discovery.domain.model.SizeTier;
import com.gresk.modules.discovery.domain.port.out.UpcomingEventInfo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Ficha completa de un artista en Discovery: compone catálogo + perfil de
 * descubrimiento + próximo evento. No es un agregado de dominio — es un
 * resultado de caso de uso ensamblado a partir de varios ports.
 */
public record ArtistDiscoveryDetail(
        String artistId,
        String name,
        String origin,
        List<String> genres,
        String imageAssetId,
        String bio,
        double avgRating,
        String spotifyUrl,
        String bandcampUrl,
        SizeTier sizeTier,
        Integer spotifyPopularity,
        BigDecimal greskScore,
        int greskReviewCount,
        int greskDemandCount,
        int greskVerifiedAttendees,
        int knownByCount,
        Optional<UpcomingEventInfo> nextEvent
) {
}
