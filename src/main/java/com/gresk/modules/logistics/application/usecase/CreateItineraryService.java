package com.gresk.modules.logistics.application.usecase;

import com.gresk.modules.logistics.application.command.CreateItineraryCommand;
import com.gresk.modules.logistics.application.port.in.CreateItineraryUseCase;
import com.gresk.modules.logistics.domain.model.Itinerary;
import com.gresk.modules.logistics.domain.model.TourId;
import com.gresk.modules.logistics.domain.port.out.ItineraryRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateItineraryService implements CreateItineraryUseCase {

    private final ItineraryRepositoryPort itineraryRepository;

    @Override
    public Itinerary execute(CreateItineraryCommand command) {
        Itinerary itinerary = Itinerary.create(TourId.of(command.tourId()), PromoterId.of(command.promoterId()));
        return itineraryRepository.save(itinerary);
    }
}
