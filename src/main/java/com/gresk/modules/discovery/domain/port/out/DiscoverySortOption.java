package com.gresk.modules.discovery.domain.port.out;

public enum DiscoverySortOption {
    GRESK_SCORE,
    SMALLEST,
    MOST_RECENT,
    /** Modo "Antes que nadie": fecha de alta en el catálogo GresK, ascendente. */
    EARLIEST_DISCOVERY
}
