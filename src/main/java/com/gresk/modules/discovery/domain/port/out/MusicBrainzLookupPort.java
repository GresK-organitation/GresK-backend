package com.gresk.modules.discovery.domain.port.out;

import java.util.Optional;

/** Enriquecimiento de solo lectura vía MusicBrainz (1 req/s, ver resilience4j). */
public interface MusicBrainzLookupPort {
    Optional<MusicBrainzArtistInfo> searchByName(String artistName);
}
