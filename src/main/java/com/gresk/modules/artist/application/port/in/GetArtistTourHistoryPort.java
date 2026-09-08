package com.gresk.modules.artist.application.port.in;

import com.gresk.modules.artist.domain.port.out.TourPerformanceRecord;

import java.util.List;

public interface GetArtistTourHistoryPort {
    List<TourPerformanceRecord> execute(String artistId);
}
