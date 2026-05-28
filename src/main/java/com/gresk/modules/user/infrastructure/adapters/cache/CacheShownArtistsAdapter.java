package com.gresk.modules.user.infrastructure.adapters.cache;

import com.gresk.modules.user.domain.port.out.ShownArtistsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CacheShownArtistsAdapter implements ShownArtistsPort {

    private static final String CACHE_NAME = "shownArtists";

    private final CacheManager cacheManager;

    @Override
    @SuppressWarnings("unchecked")
    public Set<String> getShownIds(UUID userId) {
        var cache = cacheManager.getCache(CACHE_NAME);
        if (cache == null) return Set.of();
        var wrapper = cache.get(userId, Set.class);
        return wrapper != null ? (Set<String>) wrapper : Set.of();
    }

    @Override
    public void markShown(UUID userId, Set<String> spotifyArtistIds) {
        var cache = cacheManager.getCache(CACHE_NAME);
        if (cache == null) return;
        var merged = new HashSet<>(getShownIds(userId));
        merged.addAll(spotifyArtistIds);
        cache.put(userId, merged);
    }
}
