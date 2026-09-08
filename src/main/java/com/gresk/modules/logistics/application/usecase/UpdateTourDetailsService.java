package com.gresk.modules.logistics.application.usecase;

import com.gresk.modules.logistics.application.command.UpdateTourDetailsCommand;
import com.gresk.modules.logistics.application.port.in.UpdateTourDetailsUseCase;
import com.gresk.modules.logistics.domain.model.Tour;
import com.gresk.modules.logistics.domain.port.out.TourRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateTourDetailsService implements UpdateTourDetailsUseCase {

    private final TourRepositoryPort tourRepository;

    @Override
    public Tour execute(UpdateTourDetailsCommand command) {
        Tour tour = LogisticsLookups.requireTour(tourRepository, command.tourId(), command.promoterId());
        tour.updateDetails(command.name(), command.startDate(), command.endDate(), command.notes());
        return tourRepository.save(tour);
    }
}
