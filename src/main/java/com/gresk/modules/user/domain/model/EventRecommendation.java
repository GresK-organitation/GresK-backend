package com.gresk.modules.user.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public record EventRecommendation(
        String eventId,
        String title,
        String location,
        LocalDateTime dateTime,
        String imageUrl,
        String category
) {
    public EventRecommendation {
        Objects.requireNonNull(eventId, "Event ID is required");
        Objects.requireNonNull(title, "Event title is required");
        Objects.requireNonNull(dateTime, "Event date and time are required");
        Objects.requireNonNull(location, "Event location is required");

        if (title.isBlank()) throw new IllegalArgumentException("Event title cannot be empty");

        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot recommend an event that has already passed");
        }

        if (category == null || category.isBlank()) {
            category = "General";
        }
    }

    public String getFormattedDate() {
        return String.format("%02d/%02d/%d",
                dateTime.getDayOfMonth(),
                dateTime.getMonthValue(),
                dateTime.getYear());
    }

    public boolean isTonight() {
        return dateTime.toLocalDate().equals(LocalDateTime.now().toLocalDate());
    }
}