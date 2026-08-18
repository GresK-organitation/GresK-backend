package com.gresk.modules.tendencias.stats.application.usecase;

import com.gresk.modules.tendencias.stats.application.dto.MostVisitedVenueResponse;
import com.gresk.modules.tendencias.stats.application.query.MostVisitedVenueQuery;
import com.gresk.modules.tendencias.stats.infrastructure.persistence.TendenciasVenueQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetMostVisitedVenueUseCase {

    private final TendenciasVenueQueryRepository repository;

    @Cacheable(cacheNames = "tendenciasMostVisitedVenue", cacheManager = "tendenciasCacheManager")
    public List<MostVisitedVenueResponse> execute(MostVisitedVenueQuery query) {
        return repository.findMostVisitedVenues(query.from(), query.to(), query.limit()).stream()
                .map(row -> new MostVisitedVenueResponse(row.getVenueName(), row.getAttendeeCount(), row.getEventCount()))
                .toList();
    }
}
