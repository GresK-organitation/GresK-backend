package com.gresk.modules.review.application.usecase;

import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.event.domain.port.out.EventRepository;
import com.gresk.modules.review.domain.port.out.ReviewRepository;
import com.gresk.modules.review.infrastructure.web.AttendedEventResponse;
import com.gresk.modules.review.infrastructure.web.AttendedEventResponseMapper;
import com.gresk.modules.ticket.domain.port.out.TicketRepository;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetUserAttendedEventsUseCase {

    private final TicketRepository             ticketRepository;
    private final EventRepository              eventRepository;
    private final ReviewRepository             reviewRepository;
    private final AttendedEventResponseMapper  mapper;

    @Transactional(readOnly = true)
    public List<AttendedEventResponse> execute(UUID userId) {
        UserId uid = UserId.of(userId);

        return ticketRepository.findByUserId(uid).stream()
                .map(ticket -> {
                    Event event = eventRepository.findById(ticket.getEventId())
                            .orElseThrow(() -> new IllegalStateException(
                                    "Event not found for ticket: " + ticket.getId()));
                    return mapper.toResponse(ticket, event,
                            reviewRepository.findByTicketId(ticket.getId()).orElse(null));
                })
                .toList();
    }
}
