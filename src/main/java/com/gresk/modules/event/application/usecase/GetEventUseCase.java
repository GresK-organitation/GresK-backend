package com.gresk.modules.event.application.usecase;

import com.gresk.modules.event.application.query.GetEventQuery;
import com.gresk.modules.event.domain.exception.EventNotFoundException;
import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.event.domain.port.out.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetEventUseCase {

    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    public Event execute(GetEventQuery query) {
        EventId id = EventId.of(query.eventId());
        return eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(query.eventId()));
    }
}
