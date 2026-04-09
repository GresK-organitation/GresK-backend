package com.gresk.modules.event.domain.model;

import com.gresk.modules.event.domain.exception.IncompleteEventException;
import com.gresk.modules.event.domain.exception.InvalidEventTransitionException;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.time.LocalDateTime;

public final class Event {

    private final EventId id;
    private final String title;
    private final PromoterId promoterId;
    private final LocalDateTime createdAt;

    private EventStatus status;
    private Genre genre;
    private Price price;
    private Capacity capacity;
    private LocalDateTime eventDate;
    private Location location;
    private LocalDateTime revealAt;

    private Event(EventId id, String title, PromoterId promoterId, Genre genre,
                  Price price, Capacity capacity, LocalDateTime eventDate,
                  Location location, LocalDateTime revealAt,
                  EventStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.promoterId = promoterId;
        this.genre = genre;
        this.price = price;
        this.capacity = capacity;
        this.eventDate = eventDate;
        this.location = location;
        this.revealAt = revealAt;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Event create(String title, PromoterId promoterId) {
        return new Event(
                EventId.generate(), title, promoterId,
                null, null, null, null, null, null,
                EventStatus.DRAFT, LocalDateTime.now()
        );
    }

    public static Event reconstitute(EventId id, String title, PromoterId promoterId,
                               Genre genre, Price price, Capacity capacity,
                               LocalDateTime eventDate, Location location,
                               LocalDateTime revealAt, EventStatus status,
                               LocalDateTime createdAt) {
        return new Event(id, title, promoterId, genre, price, capacity,
                eventDate, location, revealAt, status, createdAt);
    }

    public void publish() {
        if (genre == null) {
            throw new IncompleteEventException("Cannot publish event: genre is missing");
        }
        if (price == null) {
            throw new IncompleteEventException("Cannot publish event: price is missing");
        }
        if (capacity == null) {
            throw new IncompleteEventException("Cannot publish event: capacity is missing");
        }
        if (eventDate == null) {
            throw new IncompleteEventException("Cannot publish event: eventDate is missing");
        }
        if (!status.canTransitionTo(EventStatus.PUBLISHED)) {
            throw new InvalidEventTransitionException(
                    "Cannot transition from " + status + " to PUBLISHED");
        }
        this.status = EventStatus.PUBLISHED;
    }

    public void finish() {
        if (!status.canTransitionTo(EventStatus.FINISHED)) {
            throw new InvalidEventTransitionException(
                    "Cannot transition from " + status + " to FINISHED");
        }
        this.status = EventStatus.FINISHED;
    }

    public void cancel() {
        if (!status.canTransitionTo(EventStatus.CANCELLED)) {
            throw new InvalidEventTransitionException(
                    "Cannot transition from " + status + " to CANCELLED");
        }
        this.status = EventStatus.CANCELLED;
    }

    public Event withGenre(Genre genre) {
        this.genre = genre;
        return this;
    }

    public Event withPrice(Price price) {
        this.price = price;
        return this;
    }

    public Event withCapacity(Capacity capacity) {
        this.capacity = capacity;
        return this;
    }

    public Event withEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
        return this;
    }

    public Event withLocation(Location location) {
        this.location = location;
        return this;
    }

    public Event withRevealAt(LocalDateTime revealAt) {
        this.revealAt = revealAt;
        return this;
    }

    public EventId getId() { return id; }
    public String getTitle() { return title; }
    public PromoterId getPromoterId() { return promoterId; }
    public EventStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Genre getGenre() { return genre; }
    public Price getPrice() { return price; }
    public Capacity getCapacity() { return capacity; }
    public LocalDateTime getEventDate() { return eventDate; }
    public Location getLocation() { return location; }
    public LocalDateTime getRevealAt() { return revealAt; }

    public void decrementCapacity() {
    }
}
