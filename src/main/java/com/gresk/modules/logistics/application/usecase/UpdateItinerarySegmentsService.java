package com.gresk.modules.logistics.application.usecase;

import com.gresk.modules.logistics.application.command.UpdateItinerarySegmentsCommand;
import com.gresk.modules.logistics.application.port.in.UpdateItinerarySegmentsUseCase;
import com.gresk.modules.logistics.domain.model.Itinerary;
import com.gresk.modules.logistics.domain.port.out.ItineraryRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateItinerarySegmentsService implements UpdateItinerarySegmentsUseCase {

    private final ItineraryRepositoryPort itineraryRepository;

    @Override
    public Itinerary execute(UpdateItinerarySegmentsCommand command) {
        Itinerary itinerary = LogisticsLookups.requireItineraryById(itineraryRepository, command.itineraryId(), command.promoterId());
        itinerary.replaceSegments(LogisticsInputMapper.toItinerarySegments(command.segments()));
        return itineraryRepository.save(itinerary);
    }
}
