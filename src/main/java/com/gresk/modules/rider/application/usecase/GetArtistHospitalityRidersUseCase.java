package com.gresk.modules.rider.application.usecase;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.rider.domain.model.HospitalityRider;
import com.gresk.modules.rider.domain.port.out.HospitalityRiderRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetArtistHospitalityRidersUseCase {

    private final HospitalityRiderRepositoryPort riderRepository;

    @Transactional(readOnly = true)
    public List<HospitalityRider> execute(String artistId) {
        return riderRepository.findByArtistId(ArtistId.of(artistId));
    }
}
