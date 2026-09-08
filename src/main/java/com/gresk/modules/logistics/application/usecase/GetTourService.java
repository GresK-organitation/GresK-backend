package com.gresk.modules.logistics.application.usecase;

import com.gresk.modules.logistics.application.port.in.GetTourUseCase;
import com.gresk.modules.logistics.application.query.GetTourQuery;
import com.gresk.modules.logistics.domain.model.Tour;
import com.gresk.modules.logistics.domain.port.out.TourRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetTourService implements GetTourUseCase {

    private final TourRepositoryPort tourRepository;

    @Override
    public Tour execute(GetTourQuery query) {
        return LogisticsLookups.requireTour(tourRepository, query.tourId(), query.promoterId());
    }
}
