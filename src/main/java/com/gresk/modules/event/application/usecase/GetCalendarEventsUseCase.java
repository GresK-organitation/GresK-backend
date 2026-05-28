package com.gresk.modules.event.application.usecase;

import com.gresk.modules.event.application.query.CalendarEventsQuery;
import com.gresk.modules.event.domain.model.CalendarEvent;
import com.gresk.modules.event.domain.port.out.EventRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetCalendarEventsUseCase {

    private final EventRepository eventRepository;

    public List<CalendarEvent> execute(CalendarEventsQuery query) {
        PromoterId promoterId = PromoterId.of(query.promoterId());
        return eventRepository.findCalendarEventsByPromoter(promoterId, query.from(), query.to());
    }
}
