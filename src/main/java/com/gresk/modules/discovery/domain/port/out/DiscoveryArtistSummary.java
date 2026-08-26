package com.gresk.modules.discovery.domain.port.out;

import com.gresk.modules.discovery.domain.model.SizeTier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Fila de lectura combinando catálogo (`artists`) + perfil de descubrimiento
 * (`artist_discovery_profile`), tal y como la necesita la tarjeta de la
 * lista de Discovery. No es un agregado de dominio — es un DTO de consulta.
 */
public record DiscoveryArtistSummary(
        String artistId,
        String name,
        String origin,
        List<String> genres,
        String imageAssetId,
        SizeTier sizeTier,
        Integer spotifyPopularity,
        BigDecimal greskScore,
        double avgRating,
        int greskReviewCount,
        int greskDemandCount,
        boolean hasUpcomingEvents,
        LocalDate nextEventDate,
        String nextEventCity
) {
}
