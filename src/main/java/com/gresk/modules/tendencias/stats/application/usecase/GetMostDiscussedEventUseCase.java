package com.gresk.modules.tendencias.stats.application.usecase;

import com.gresk.modules.tendencias.stats.application.dto.MostDiscussedEventResponse;
import com.gresk.modules.tendencias.stats.application.query.MostDiscussedEventQuery;
import com.gresk.modules.tendencias.stats.infrastructure.persistence.TendenciasEventQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetMostDiscussedEventUseCase {

    private final TendenciasEventQueryRepository repository;

    @Cacheable(cacheNames = "tendenciasMostDiscussedEvent", cacheManager = "tendenciasCacheManager")
    public List<MostDiscussedEventResponse> execute(MostDiscussedEventQuery query) {
        return repository.findMostDiscussedEvents(query.from(), query.to(), query.limit()).stream()
                .map(row -> new MostDiscussedEventResponse(row.getEventId(), row.getEventTitle(), row.getReviewCount()))
                .toList();
    }
}
