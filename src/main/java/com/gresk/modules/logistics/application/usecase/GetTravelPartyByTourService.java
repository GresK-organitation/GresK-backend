package com.gresk.modules.logistics.application.usecase;

import com.gresk.modules.logistics.application.port.in.GetTravelPartyByTourUseCase;
import com.gresk.modules.logistics.application.query.GetTravelPartyByTourQuery;
import com.gresk.modules.logistics.domain.model.TourId;
import com.gresk.modules.logistics.domain.model.TravelParty;
import com.gresk.modules.logistics.domain.port.out.TravelPartyRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetTravelPartyByTourService implements GetTravelPartyByTourUseCase {

    private final TravelPartyRepositoryPort travelPartyRepository;

    @Override
    public TravelParty execute(GetTravelPartyByTourQuery query) {
        return LogisticsLookups.requireTravelPartyByTour(travelPartyRepository, TourId.of(query.tourId()), query.promoterId());
    }
}
