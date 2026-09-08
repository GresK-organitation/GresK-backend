package com.gresk.modules.rider.application.usecase;

import com.gresk.modules.artist.domain.exception.ArtistNotFoundException;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.port.out.ArtistRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.rider.application.command.CreateHospitalityRiderCommand;
import com.gresk.modules.rider.domain.model.HospitalityRider;
import com.gresk.modules.rider.domain.port.out.HospitalityRiderRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateHospitalityRiderUseCase {

    private final HospitalityRiderRepositoryPort riderRepository;
    private final ArtistRepositoryPort artistRepository;

    @Transactional
    public HospitalityRider execute(CreateHospitalityRiderCommand command) {
        ArtistId artistId = ArtistId.of(command.artistId());
        PromoterId promoterId = PromoterId.of(command.promoterId());

        artistRepository.findByIdAndPromoterId(artistId, promoterId)
                .orElseThrow(() -> new ArtistNotFoundException(command.artistId()));

        HospitalityRider rider = HospitalityRider.create(artistId, promoterId, command.name());
        return riderRepository.save(rider);
    }
}
