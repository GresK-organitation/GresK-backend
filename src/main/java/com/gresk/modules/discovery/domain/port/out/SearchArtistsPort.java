package com.gresk.modules.discovery.domain.port.out;

import java.util.List;

public interface SearchArtistsPort {
    List<DiscoveryArtistSummary> search(DiscoveryFilters filters);
    long count(DiscoveryFilters filters);
}
