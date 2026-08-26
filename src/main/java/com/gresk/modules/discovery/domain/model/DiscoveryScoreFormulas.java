package com.gresk.modules.discovery.domain.model;

import com.gresk.modules.discovery.domain.port.out.DiscoveryProfileSignals;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * El GresK Score: en vez de ordenar por popularidad (que siempre favorece a
 * los mismos artistas), pondera comunidad real de GresK, pequeñez respecto
 * a Spotify, actividad en vivo y momentum de demanda reciente. Pura, sin
 * dependencias de infraestructura — solo consume {@link DiscoveryProfileSignals}.
 */
final class DiscoveryScoreFormulas {

    private static final BigDecimal TEN = BigDecimal.TEN;
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private static final BigDecimal COMMUNITY_WEIGHT = new BigDecimal("0.40");
    private static final BigDecimal SMALLNESS_WEIGHT  = new BigDecimal("0.30");
    private static final BigDecimal LIVE_WEIGHT       = new BigDecimal("0.20");
    private static final BigDecimal MOMENTUM_WEIGHT   = new BigDecimal("0.10");

    private DiscoveryScoreFormulas() {}

    static BigDecimal greskScore(DiscoveryProfileSignals signals) {
        BigDecimal score = communityScore(signals).multiply(COMMUNITY_WEIGHT)
                .add(smallnessScore(signals).multiply(SMALLNESS_WEIGHT))
                .add(liveScore(signals).multiply(LIVE_WEIGHT))
                .add(momentumScore(signals).multiply(MOMENTUM_WEIGHT));
        return score.max(ZERO).min(TEN).setScale(2, RoundingMode.HALF_UP);
    }

    /** Premia reseñas, demanda y asistencia verificada — comunidad real, no oyentes pasivos. */
    static BigDecimal communityScore(DiscoveryProfileSignals signals) {
        BigDecimal raw = BigDecimal.valueOf(signals.reviewCount()).multiply(new BigDecimal("0.5"))
                .add(BigDecimal.valueOf(signals.demandCount()).multiply(new BigDecimal("0.3")))
                .add(BigDecimal.valueOf(signals.verifiedAttendeesCount()).multiply(new BigDecimal("0.8")));
        return raw.min(TEN).max(ZERO);
    }

    /**
     * Inversamente proporcional a la popularidad de Spotify. Sin vínculo a
     * Spotify se trata como el caso más nicho posible (10.0) — típico de
     * artistas hiperlocales que todavía no tienen presencia en streaming.
     */
    static BigDecimal smallnessScore(DiscoveryProfileSignals signals) {
        if (signals.spotifyPopularity() == null) return TEN;
        BigDecimal popularity = BigDecimal.valueOf(signals.spotifyPopularity());
        BigDecimal raw = TEN.subtract(popularity.divide(BigDecimal.TEN, 10, RoundingMode.HALF_UP));
        return raw.max(ZERO).min(TEN);
    }

    /** Premia tener conciertos próximos en el catálogo GresK. */
    static BigDecimal liveScore(DiscoveryProfileSignals signals) {
        return signals.hasUpcomingEvents() ? new BigDecimal("8.0") : new BigDecimal("3.0");
    }

    /** Crecimiento de señales de demanda en las últimas 4 semanas vs las 4 anteriores. */
    static BigDecimal momentumScore(DiscoveryProfileSignals signals) {
        long recent = signals.recentDemandSignals();
        long previous = signals.previousDemandSignals();
        if (previous == 0) return recent > 0 ? new BigDecimal("8.0") : ZERO;
        BigDecimal growth = BigDecimal.valueOf(recent - previous)
                .divide(BigDecimal.valueOf(previous), 10, RoundingMode.HALF_UP);
        return growth.multiply(TEN).min(TEN);
    }
}
