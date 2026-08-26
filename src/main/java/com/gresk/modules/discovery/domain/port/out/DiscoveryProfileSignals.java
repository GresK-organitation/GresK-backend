package com.gresk.modules.discovery.domain.port.out;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Señales agregadas de un artista, ya resueltas por infraestructura, sobre
 * las que opera {@code DiscoveryScoreFormulas}. El dominio nunca ve
 * entidades JPA ni datos crudos de otros módulos — solo este DTO.
 */
public record DiscoveryProfileSignals(
        Integer spotifyPopularity,
        long reviewCount,
        long demandCount,
        long verifiedAttendeesCount,
        long knownByCount,
        long recentDemandSignals,
        long previousDemandSignals,
        boolean hasUpcomingEvents,
        LocalDate nextEventDate,
        String nextEventCity,
        Instant artistCreatedAt,
        String musicBrainzId,
        String mbCountry,
        String mbCity,
        Integer mbBeginYear
) {
}
