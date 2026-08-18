package com.gresk.modules.tendencias.stats.application.usecase;

import com.gresk.modules.tendencias.stats.application.dto.HighestSellThroughResponse;
import com.gresk.modules.tendencias.stats.application.query.HighestSellThroughQuery;
import com.gresk.modules.tendencias.stats.infrastructure.persistence.TendenciasEventQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetHighestSellThroughUseCase {

    private final TendenciasEventQueryRepository repository;

    @Cacheable(cacheNames = "tendenciasHighestSellThrough", cacheManager = "tendenciasCacheManager")
    public List<HighestSellThroughResponse> execute(HighestSellThroughQuery query) {
        return repository.findHighestSellThrough(query.from(), query.to(), query.limit()).stream()
                .map(row -> new HighestSellThroughResponse(row.getEventId(), row.getEventTitle(), row.getSellThroughPercent()))
                .toList();
    }
}
