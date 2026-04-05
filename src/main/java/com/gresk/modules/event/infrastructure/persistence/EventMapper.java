package com.gresk.modules.event.infrastructure.persistence;

import com.gresk.modules.event.domain.model.*;
import com.gresk.modules.promoter.domain.valueobject.PromoterId;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EventMapper {

    public Event toDomain(EventEntity entity) {
        Price price = entity.getAmount() != null
                ? new Price(entity.getAmount(), entity.getCurrency()) : null;

        Capacity capacity = entity.getTotalCapacity() != null
                ? new Capacity(entity.getTotalCapacity(), entity.getAvailableCapacity()) : null;

        Location location = entity.getCity() != null
                ? new Location(entity.getCity(), entity.getAddress(), entity.getVenue()) : null;

        return Event.reconstitute(
                EventId.of(entity.getId().toString()),
                entity.getTitle(),
                PromoterId.of(entity.getPromoterId().toString()),
                entity.getGenre(),
                price,
                capacity,
                entity.getEventDate(),
                location,
                entity.getRevealAt(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }

    public EventEntity toEntity(Event event) {
        return EventEntity.builder()
                .id(event.getId().value())
                .title(event.getTitle())
                .promoterId(event.getPromoterId().value())
                .status(event.getStatus())
                .genre(event.getGenre())
                .amount(event.getPrice() != null ? event.getPrice().amount() : null)
                .currency(event.getPrice() != null ? event.getPrice().currency() : null)
                .totalCapacity(event.getCapacity() != null ? event.getCapacity().total() : null)
                .availableCapacity(event.getCapacity() != null ? event.getCapacity().available() : null)
                .eventDate(event.getEventDate())
                .city(event.getLocation() != null ? event.getLocation().city() : null)
                .address(event.getLocation() != null ? event.getLocation().address() : null)
                .venue(event.getLocation() != null ? event.getLocation().venue() : null)
                .revealAt(event.getRevealAt())
                .createdAt(event.getCreatedAt() != null ? event.getCreatedAt() : LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
