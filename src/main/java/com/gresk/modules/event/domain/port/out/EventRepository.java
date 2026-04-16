package com.gresk.modules.event.domain.port.out;

import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.event.domain.model.EventId;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

public interface EventRepository {
    Event save(Event event);
    Optional<Event> findById(EventId id);
    List<Event> findAll(EventFilter filter, PageRequest pageRequest);
    long count(EventFilter filter);
    boolean     existsById(EventId id);
    List<Event> findLastMinute();
}
