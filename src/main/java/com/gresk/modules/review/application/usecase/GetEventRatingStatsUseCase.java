package com.gresk.modules.review.application.usecase;

import com.gresk.modules.event.domain.exception.EventNotFoundException;
import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.event.domain.model.EventRatingStats;
import com.gresk.modules.event.domain.port.out.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetEventRatingStatsUseCase {

    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    public EventRatingStats execute(GetEventRatingStatsQuery query) {
        return eventRepository.findById(EventId.of(query.eventId()))
                .orElseThrow(() -> new EventNotFoundException(query.eventId()))
                .getRatingStats();
    }
}
