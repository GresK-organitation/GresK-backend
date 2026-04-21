package com.gresk.modules.review.infrastructure.web;

import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.review.domain.model.Review;
import com.gresk.modules.ticket.domain.model.Ticket;
import org.springframework.stereotype.Component;

@Component
public class AttendedEventResponseMapper {

    public AttendedEventResponse toResponse(Ticket ticket, Event event, Review review) {
        boolean pending = review == null;

        String venue = null;
        if (event.getLocation() != null) {
            venue = event.getLocation().venue() != null
                    ? event.getLocation().venue()
                    : (event.getLocation().address() != null
                    ? event.getLocation().address().city().value() : null);
        }

        String coverImageUrl = event.getCoverImage() != null && !event.getCoverImage().isEmpty()
                ? event.getCoverImage().value() : null;

        String genre = event.getGenre() != null ? event.getGenre().name() : null;

        String date = event.getEventDate() != null
                ? event.getEventDate().toString().substring(0, 10) : null;

        if (pending) {
            return new AttendedEventResponse(
                    ticket.getId().value().toString(),
                    event.getId().value().toString(),
                    event.getTitle(),
                    venue, coverImageUrl, genre, date,
                    true, null,
                    0, 0, 0, 0, 0, 0,
                    null, null, 0,
                    event.getRatingStats().avgOverallRating()
            );
        }

        String comment  = review.getComment() != null ? review.getComment().value() : null;
        String photoUrl = review.getPhotoUrl() != null && !review.getPhotoUrl().isEmpty()
                ? review.getPhotoUrl().value() : null;

        return new AttendedEventResponse(
                ticket.getId().value().toString(),
                event.getId().value().toString(),
                event.getTitle(),
                venue, coverImageUrl, genre, date,
                false,
                review.getId().value().toString(),
                review.getOverallRating().value(),
                review.getDetailedRating().artistRating().value(),
                review.getDetailedRating().soundRating().value(),
                review.getDetailedRating().ambienceRating().value(),
                review.getDetailedRating().venueRating().value(),
                review.getDetailedRating().setlistRating().value(),
                comment, photoUrl,
                review.getPointsAwarded(),
                event.getRatingStats().avgOverallRating()
        );
    }
}
