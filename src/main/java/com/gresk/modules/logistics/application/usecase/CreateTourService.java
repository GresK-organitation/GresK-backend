package com.gresk.modules.logistics.application.usecase;

import com.gresk.modules.logistics.application.command.CreateTourCommand;
import com.gresk.modules.logistics.application.port.in.CreateTourUseCase;
import com.gresk.modules.logistics.domain.model.Tour;
import com.gresk.modules.logistics.domain.port.out.TourRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateTourService implements CreateTourUseCase {

    private final TourRepositoryPort tourRepository;

    @Override
    public Tour execute(CreateTourCommand command) {
        Tour tour = Tour.create(PromoterId.of(command.promoterId()), UUID.fromString(command.artistId()),
                command.name(), command.startDate(), command.endDate(), command.notes());
        tour.replaceLegs(LogisticsInputMapper.toTourLegs(command.legs()));
        return tourRepository.save(tour);
    }
}
