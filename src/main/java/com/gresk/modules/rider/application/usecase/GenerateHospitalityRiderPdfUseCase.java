package com.gresk.modules.rider.application.usecase;

import com.gresk.modules.artist.domain.model.Artist;
import com.gresk.modules.artist.domain.port.out.ArtistRepositoryPort;
import com.gresk.modules.rider.domain.exception.RiderNotFoundException;
import com.gresk.modules.rider.domain.model.HospitalityRider;
import com.gresk.modules.rider.domain.model.RiderId;
import com.gresk.modules.rider.domain.port.out.HospitalityRiderRepositoryPort;
import com.gresk.modules.rider.infrastructure.pdf.HospitalityRiderPdfGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GenerateHospitalityRiderPdfUseCase {

    private final HospitalityRiderRepositoryPort riderRepository;
    private final ArtistRepositoryPort artistRepository;
    private final HospitalityRiderPdfGenerator pdfGenerator;

    @Transactional(readOnly = true)
    public byte[] execute(String riderId) {
        HospitalityRider rider = riderRepository.findById(RiderId.of(riderId))
                .orElseThrow(() -> new RiderNotFoundException(riderId));

        String artistName = artistRepository.findById(rider.getArtistId())
                .map(Artist::getName)
                .map(name -> name.value())
                .orElse(null);

        return pdfGenerator.generate(rider, artistName);
    }
}
