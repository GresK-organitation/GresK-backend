package com.gresk.modules.review.infrastructure.adapter;

import com.gresk.modules.event.domain.exception.EventNotFoundException;
import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.event.domain.port.out.EventRepository;
import com.gresk.modules.review.domain.port.out.EventRatingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventRatingAdapter implements EventRatingPort {

    private final EventRepository eventRepository;

    @Override
    public void addRating(EventId eventId,
                          int artist, int sound, int ambience,
                          int venue, int setlist, int overall) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(
                        "Event not found: " + eventId.value()));

        event.addRating(artist, sound, ambience, venue, setlist, overall);
        eventRepository.save(event);
    }
}
