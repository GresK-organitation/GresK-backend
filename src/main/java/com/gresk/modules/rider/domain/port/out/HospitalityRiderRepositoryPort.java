package com.gresk.modules.rider.domain.port.out;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.rider.domain.model.HospitalityRider;
import com.gresk.modules.rider.domain.model.RiderId;

import java.util.List;
import java.util.Optional;

public interface HospitalityRiderRepositoryPort {

    HospitalityRider save(HospitalityRider rider);

    Optional<HospitalityRider> findById(RiderId id);

    Optional<HospitalityRider> findByShareToken(String shareToken);

    List<HospitalityRider> findByArtistId(ArtistId artistId);
}
