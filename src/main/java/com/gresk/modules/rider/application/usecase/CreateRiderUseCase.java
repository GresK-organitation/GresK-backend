package com.gresk.modules.rider.application.usecase;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.port.out.ArtistRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.rider.application.command.CreateRiderCommand;
import com.gresk.modules.rider.domain.model.TechnicalRider;
import com.gresk.modules.rider.domain.port.out.RiderRepositoryPort;
import com.gresk.modules.artist.domain.exception.ArtistNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateRiderUseCase {

    private final RiderRepositoryPort riderRepository;
    private final ArtistRepositoryPort artistRepository;

    @Transactional
    public TechnicalRider execute(CreateRiderCommand command) {
        ArtistId artistId   = ArtistId.of(command.artistId());
        PromoterId promoterId = PromoterId.of(command.promoterId());

        artistRepository.findByIdAndPromoterId(artistId, promoterId)
                .orElseThrow(() -> new ArtistNotFoundException(command.artistId()));

        TechnicalRider rider = TechnicalRider.create(artistId, promoterId, command.name());
        return riderRepository.save(rider);
    }
}
