package com.gresk.modules.logistics.application.usecase;

import com.gresk.modules.logistics.application.command.UpdateTourLegsCommand;
import com.gresk.modules.logistics.application.port.in.UpdateTourLegsUseCase;
import com.gresk.modules.logistics.domain.model.Tour;
import com.gresk.modules.logistics.domain.port.out.TourRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateTourLegsService implements UpdateTourLegsUseCase {

    private final TourRepositoryPort tourRepository;

    @Override
    public Tour execute(UpdateTourLegsCommand command) {
        Tour tour = LogisticsLookups.requireTour(tourRepository, command.tourId(), command.promoterId());
        tour.replaceLegs(LogisticsInputMapper.toTourLegs(command.legs()));
        return tourRepository.save(tour);
    }
}
