package com.gresk.modules.artist.application.usecase;

import com.gresk.modules.artist.application.port.in.GetArtistTourHistoryPort;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.port.out.ArtistTourHistoryPort;
import com.gresk.modules.artist.domain.port.out.TourPerformanceRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetArtistTourHistoryUseCase implements GetArtistTourHistoryPort {

    private final ArtistTourHistoryPort tourHistoryPort;

    @Override
    public List<TourPerformanceRecord> execute(String artistId) {
        return tourHistoryPort.findPastPerformances(ArtistId.of(artistId));
    }
}
