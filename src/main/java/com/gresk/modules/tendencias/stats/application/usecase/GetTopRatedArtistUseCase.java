package com.gresk.modules.tendencias.stats.application.usecase;

import com.gresk.modules.tendencias.stats.application.dto.TopRatedArtistResponse;
import com.gresk.modules.tendencias.stats.application.query.TopRatedArtistQuery;
import com.gresk.modules.tendencias.stats.infrastructure.persistence.TendenciasArtistQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetTopRatedArtistUseCase {

    private final TendenciasArtistQueryRepository repository;

    @Cacheable(cacheNames = "tendenciasTopRatedArtist", cacheManager = "tendenciasCacheManager")
    public List<TopRatedArtistResponse> execute(TopRatedArtistQuery query) {
        return repository.findTopRatedArtists(query.minReviews(), query.limit()).stream()
                .map(row -> new TopRatedArtistResponse(row.getArtistId(), row.getArtistName(), row.getAvgRating(), row.getReviewCount()))
                .toList();
    }
}
