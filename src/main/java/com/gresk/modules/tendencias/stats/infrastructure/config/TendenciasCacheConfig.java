package com.gresk.modules.tendencias.stats.infrastructure.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * CacheManager dedicado a las tendencias de datos propios (TTL corto), aislado
 * del ConcurrentMapCacheManager por defecto que usa el resto de la app (que no
 * soporta expiración) para no afectar cachés existentes como "shownArtists".
 */
@Configuration
public class TendenciasCacheConfig {

    @Bean
    public CacheManager tendenciasCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "tendenciasMostReviewedArtist",
                "tendenciasTopRatedArtist",
                "tendenciasMostVisitedVenue",
                "tendenciasTrendingGenre",
                "tendenciasMostDiscussedEvent",
                "tendenciasHighestSellThrough"
        );
        cacheManager.setCaffeine(Caffeine.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES));
        return cacheManager;
    }
}
