package com.gresk.modules.tendencias.stats.application.usecase;

import com.gresk.modules.tendencias.stats.application.dto.TrendingGenreResponse;
import com.gresk.modules.tendencias.stats.application.query.TrendingGenreQuery;
import com.gresk.modules.tendencias.stats.infrastructure.persistence.GenreCountRow;
import com.gresk.modules.tendencias.stats.infrastructure.persistence.TendenciasGenreQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GetTrendingGenreUseCase {

    private final TendenciasGenreQueryRepository repository;

    @Cacheable(cacheNames = "tendenciasTrendingGenre", cacheManager = "tendenciasCacheManager")
    public List<TrendingGenreResponse> execute(TrendingGenreQuery query) {
        Map<String, Long> currentCounts = toMap(repository.countEventsByGenre(query.currentFrom(), query.currentTo()));
        Map<String, Long> previousCounts = toMap(repository.countEventsByGenre(query.previousFrom(), query.previousTo()));

        Map<String, Long> merged = new HashMap<>(currentCounts);
        previousCounts.keySet().forEach(genre -> merged.putIfAbsent(genre, 0L));

        return merged.keySet().stream()
                .map(genre -> {
                    long current = currentCounts.getOrDefault(genre, 0L);
                    long previous = previousCounts.getOrDefault(genre, 0L);
                    double growth = previous == 0
                            ? (current > 0 ? 100.0 : 0.0)
                            : ((current - previous) * 100.0) / previous;
                    return new TrendingGenreResponse(genre, current, previous, growth);
                })
                .sorted(Comparator.comparingDouble(TrendingGenreResponse::growthPercent).reversed())
                .limit(query.limit())
                .toList();
    }

    private Map<String, Long> toMap(List<GenreCountRow> rows) {
        Map<String, Long> map = new HashMap<>();
        rows.forEach(row -> map.put(row.getGenre(), row.getEventCount()));
        return map;
    }
}
