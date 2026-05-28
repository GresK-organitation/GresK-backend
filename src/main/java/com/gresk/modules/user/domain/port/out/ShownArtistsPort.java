package com.gresk.modules.user.domain.port.out;

import java.util.Set;
import java.util.UUID;

public interface ShownArtistsPort {
    Set<String> getShownIds(UUID userId);
    void markShown(UUID userId, Set<String> spotifyArtistIds);
}
