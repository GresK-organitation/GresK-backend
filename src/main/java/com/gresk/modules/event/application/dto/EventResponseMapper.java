package com.gresk.modules.event.application.dto;

import com.gresk.modules.event.domain.model.Event;
import org.springframework.stereotype.Component;

@Component
public class EventResponseMapper {

    public EventResponse toResponse(Event event) {
        return new EventResponse(
                event.getId().toString(),
                event.getTitle(),
                event.getPromoterId().toString(),
                event.getStatus().name(),
                event.getGenre()    != null ? event.getGenre().name()         : null,
                event.getPrice()    != null ? event.getPrice().amount()       : null,
                event.getPrice()    != null ? event.getPrice().currency()     : null,
                event.getCapacity() != null ? event.getCapacity().total()     : null,
                event.getCapacity() != null ? event.getCapacity().available() : null,
                event.getEventDate(),
                event.getLocation() != null ? event.getLocation().city()      : null,
                event.getLocation() != null ? event.getLocation().address()   : null,
                event.getLocation() != null ? event.getLocation().venue()     : null,
                event.getRevealAt(),
                event.getCreatedAt()
        );
    }
}
