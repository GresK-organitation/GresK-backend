package com.gresk.modules.discovery.application.usecase;

import com.gresk.modules.discovery.domain.port.out.DiscoveryArtistSummary;
import com.gresk.modules.discovery.domain.port.out.DiscoveryFilters;
import com.gresk.modules.discovery.domain.port.out.SearchArtistsPort;
import com.gresk.shared.application.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchDiscoveryArtistsUseCase {

    private final SearchArtistsPort searchArtistsPort;

    public PageResponse<DiscoveryArtistSummary> execute(DiscoveryFilters filters) {
        var content = searchArtistsPort.search(filters);
        long total = searchArtistsPort.count(filters);
        return PageResponse.of(content, total, PageRequest.of(filters.page(), filters.size()));
    }
}
