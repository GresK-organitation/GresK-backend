package com.gresk.modules.rider.domain.port.out;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.rider.domain.model.TechnicalRider;
import com.gresk.modules.rider.domain.model.RiderId;

import java.util.List;
import java.util.Optional;

public interface RiderRepositoryPort {

    TechnicalRider save(TechnicalRider rider);

    Optional<TechnicalRider> findById(RiderId id);

    Optional<TechnicalRider> findByShareToken(String shareToken);

    List<TechnicalRider> findByArtistId(ArtistId artistId);

    boolean existsById(RiderId id);
}
