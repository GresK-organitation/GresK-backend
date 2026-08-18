package com.gresk.modules.tendencias.stats.application.usecase;

import com.gresk.modules.tendencias.stats.application.dto.MostReviewedArtistResponse;
import com.gresk.modules.tendencias.stats.application.query.MostReviewedArtistQuery;
import com.gresk.modules.tendencias.stats.infrastructure.persistence.TendenciasArtistQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetMostReviewedArtistUseCase {

    private final TendenciasArtistQueryRepository repository;

    @Cacheable(cacheNames = "tendenciasMostReviewedArtist", cacheManager = "tendenciasCacheManager")
    public List<MostReviewedArtistResponse> execute(MostReviewedArtistQuery query) {
        return repository.findMostReviewedArtists(query.from(), query.to(), query.limit()).stream()
                .map(row -> new MostReviewedArtistResponse(row.getArtistId(), row.getArtistName(), row.getReviewCount()))
                .toList();
    }
}
