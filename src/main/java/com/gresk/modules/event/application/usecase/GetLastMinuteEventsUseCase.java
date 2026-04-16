package com.gresk.modules.event.application.usecase;

import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.event.domain.port.out.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetLastMinuteEventsUseCase {

    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    public List<Event> execute() {
        return eventRepository.findLastMinute();
    }
}
