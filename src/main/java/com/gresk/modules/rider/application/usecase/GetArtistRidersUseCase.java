package com.gresk.modules.rider.application.usecase;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.rider.domain.model.TechnicalRider;
import com.gresk.modules.rider.domain.port.out.RiderRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetArtistRidersUseCase {

    private final RiderRepositoryPort riderRepository;

    @Transactional(readOnly = true)
    public List<TechnicalRider> execute(String artistId) {
        return riderRepository.findByArtistId(ArtistId.of(artistId));
    }
}
