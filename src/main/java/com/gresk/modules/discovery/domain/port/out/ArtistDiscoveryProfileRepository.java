package com.gresk.modules.discovery.domain.port.out;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.discovery.domain.model.ArtistDiscoveryProfile;

import java.util.Optional;

public interface ArtistDiscoveryProfileRepository {
    ArtistDiscoveryProfile save(ArtistDiscoveryProfile profile);
    Optional<ArtistDiscoveryProfile> findByArtistId(ArtistId artistId);
}
