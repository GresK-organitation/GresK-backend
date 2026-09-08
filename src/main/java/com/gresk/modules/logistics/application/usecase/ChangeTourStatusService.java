package com.gresk.modules.logistics.application.usecase;

import com.gresk.modules.logistics.application.command.ChangeTourStatusCommand;
import com.gresk.modules.logistics.application.port.in.ChangeTourStatusUseCase;
import com.gresk.modules.logistics.domain.exception.InvalidTourException;
import com.gresk.modules.logistics.domain.model.Tour;
import com.gresk.modules.logistics.domain.model.TourStatus;
import com.gresk.modules.logistics.domain.port.out.TourRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChangeTourStatusService implements ChangeTourStatusUseCase {

    private final TourRepositoryPort tourRepository;

    @Override
    public Tour execute(ChangeTourStatusCommand command) {
        Tour tour = LogisticsLookups.requireTour(tourRepository, command.tourId(), command.promoterId());
        TourStatus target = TourStatus.valueOf(command.targetStatus());
        switch (target) {
            case ACTIVE -> tour.activate();
            case COMPLETED -> tour.complete();
            case CANCELLED -> tour.cancel(command.reason());
            default -> throw new InvalidTourException("Unsupported target status " + target);
        }
        return tourRepository.save(tour);
    }
}
