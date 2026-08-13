package com.gresk.modules.journal.infrastructure.adapter;

import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.event.domain.port.out.EventRepository;
import com.gresk.modules.journal.domain.port.out.EventLookupPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventLookupAdapter implements EventLookupPort {

    private final EventRepository eventRepository;

    @Override
    public boolean existsById(EventId id) {
        return eventRepository.existsById(id);
    }
}
