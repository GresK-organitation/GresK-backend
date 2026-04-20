package com.gresk.modules.review.infrastructure.web.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SubmitReviewRequest(
        @NotBlank(message = "ticketId must not be blank")
        String ticketId,

        @NotBlank(message = "eventId must not be blank")
        String eventId,

        @NotNull(message = "artistRating is required")
        @Min(value = 1, message = "artistRating must be at least 1")
        @Max(value = 5, message = "artistRating must be at most 5")
        Integer artistRating,

        @NotNull(message = "soundRating is required")
        @Min(value = 1, message = "soundRating must be at least 1")
        @Max(value = 5, message = "soundRating must be at most 5")
        Integer soundRating,

        @NotNull(message = "ambienceRating is required")
        @Min(value = 1, message = "ambienceRating must be at least 1")
        @Max(value = 5, message = "ambienceRating must be at most 5")
        Integer ambienceRating,

        @NotNull(message = "venueRating is required")
        @Min(value = 1, message = "venueRating must be at least 1")
        @Max(value = 5, message = "venueRating must be at most 5")
        Integer venueRating,

        @NotNull(message = "setlistRating is required")
        @Min(value = 1, message = "setlistRating must be at least 1")
        @Max(value = 5, message = "setlistRating must be at most 5")
        Integer setlistRating,

        @Size(max = 300, message = "comment must not exceed 300 characters")
        String comment,

        String photoUrl
) {}
