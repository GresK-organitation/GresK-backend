package com.gresk.modules.rider.application.usecase;

import com.gresk.modules.artist.domain.model.Artist;
import com.gresk.modules.artist.domain.port.out.ArtistRepositoryPort;
import com.gresk.modules.rider.domain.exception.RiderNotFoundException;
import com.gresk.modules.rider.domain.model.RiderId;
import com.gresk.modules.rider.domain.model.TechnicalRider;
import com.gresk.modules.rider.domain.port.out.RiderRepositoryPort;
import com.gresk.modules.rider.infrastructure.pdf.RiderPdfGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GenerateRiderPdfUseCase {

    private final RiderRepositoryPort riderRepository;
    private final ArtistRepositoryPort artistRepository;
    private final RiderPdfGenerator   pdfGenerator;

    @Transactional(readOnly = true)
    public byte[] execute(String riderId) {
        TechnicalRider rider = riderRepository.findById(RiderId.of(riderId))
                .orElseThrow(() -> new RiderNotFoundException(riderId));

        String artistName = artistRepository.findById(rider.getArtistId())
                .map(Artist::getName)
                .map(name -> name.value())
                .orElse(null);

        return pdfGenerator.generate(rider, artistName);
    }
}
