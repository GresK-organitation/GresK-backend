package com.gresk.modules.logistics.application.usecase;

import com.gresk.modules.logistics.application.port.in.GetItineraryByTourUseCase;
import com.gresk.modules.logistics.application.query.GetItineraryByTourQuery;
import com.gresk.modules.logistics.domain.model.Itinerary;
import com.gresk.modules.logistics.domain.model.TourId;
import com.gresk.modules.logistics.domain.port.out.ItineraryRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetItineraryByTourService implements GetItineraryByTourUseCase {

    private final ItineraryRepositoryPort itineraryRepository;

    @Override
    public Itinerary execute(GetItineraryByTourQuery query) {
        return LogisticsLookups.requireItineraryByTour(itineraryRepository, TourId.of(query.tourId()), query.promoterId());
    }
}
