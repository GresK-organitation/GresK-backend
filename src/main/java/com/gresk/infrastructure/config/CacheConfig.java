package com.gresk.infrastructure.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    // Spring Boot auto-configures ConcurrentMapCacheManager when
    // spring-boot-starter-cache is present and no other CacheManager is defined.
    // Cache names used: "gresKArtistsByGenre", "shownArtists"
}
