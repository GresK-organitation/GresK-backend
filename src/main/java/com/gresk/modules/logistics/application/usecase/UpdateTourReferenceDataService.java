package com.gresk.modules.logistics.application.usecase;

import com.gresk.modules.logistics.application.command.UpdateTourReferenceDataCommand;
import com.gresk.modules.logistics.application.port.in.UpdateTourReferenceDataUseCase;
import com.gresk.modules.logistics.domain.model.Tour;
import com.gresk.modules.logistics.domain.port.out.TourRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateTourReferenceDataService implements UpdateTourReferenceDataUseCase {

    private final TourRepositoryPort tourRepository;

    @Override
    public Tour execute(UpdateTourReferenceDataCommand command) {
        Tour tour = LogisticsLookups.requireTour(tourRepository, command.tourId(), command.promoterId());
        tour.replaceEmergencyContacts(LogisticsInputMapper.toEmergencyContacts(command.emergencyContacts()));
        tour.replacePointsOfInterest(LogisticsInputMapper.toPointsOfInterest(command.pointsOfInterest()));
        return tourRepository.save(tour);
    }
}
