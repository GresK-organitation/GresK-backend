package com.gresk.modules.event.application.usecase;

import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.event.domain.port.out.EventFilter;
import com.gresk.modules.event.domain.port.out.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListEventsUseCase {

    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    public List<Event> execute(EventFilter filter, PageRequest pageRequest) {
        return eventRepository.findAll(filter, pageRequest);
    }

    @Transactional(readOnly = true)
    public long count(EventFilter filter) {
        return eventRepository.count(filter);
    }
}
