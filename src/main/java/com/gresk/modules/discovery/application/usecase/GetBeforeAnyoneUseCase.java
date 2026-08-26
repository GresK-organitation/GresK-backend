package com.gresk.modules.discovery.application.usecase;

import com.gresk.modules.discovery.domain.port.out.DiscoveryArtistSummary;
import com.gresk.modules.discovery.domain.port.out.DiscoveryFilters;
import com.gresk.modules.discovery.domain.port.out.DiscoverySortOption;
import com.gresk.shared.application.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Modo "Antes que nadie": artistas ordenados por fecha de entrada al
 * catálogo GresK (más recientes primero en el sentido de "acabas de
 * descubrirlo tú también"), junto al número de usuarios que ya los conocen.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetBeforeAnyoneUseCase {

    private final SearchDiscoveryArtistsUseCase searchDiscoveryArtistsUseCase;

    public PageResponse<DiscoveryArtistSummary> execute(int page, int size) {
        DiscoveryFilters filters = new DiscoveryFilters(
                java.util.Set.of(), java.util.Optional.empty(), java.util.Optional.empty(),
                java.util.Set.of(), java.util.Set.of(), java.util.Optional.empty(), java.util.Optional.empty(),
                false, false, java.util.Optional.empty(), false, false,
                DiscoverySortOption.EARLIEST_DISCOVERY, page, size
        );
        return searchDiscoveryArtistsUseCase.execute(filters);
    }
}
