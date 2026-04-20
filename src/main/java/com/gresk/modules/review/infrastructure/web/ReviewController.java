package com.gresk.modules.review.infrastructure.web;

import com.gresk.modules.review.application.usecase.GetEventRatingStatsQuery;
import com.gresk.modules.review.application.usecase.GetEventRatingStatsUseCase;
import com.gresk.modules.review.application.usecase.GetEventReviewsUseCase;
import com.gresk.modules.review.application.usecase.GetUserReviewsQuery;
import com.gresk.modules.review.application.usecase.GetUserReviewsUseCase;
import com.gresk.modules.review.application.usecase.SubmitReviewCommand;
import com.gresk.modules.review.application.usecase.UpdateReviewCommand;
import com.gresk.modules.review.domain.model.Review;
import com.gresk.modules.review.infrastructure.TransactionalSubmitReviewService;
import com.gresk.modules.review.infrastructure.TransactionalUpdateReviewService;
import com.gresk.modules.review.infrastructure.web.request.SubmitReviewRequest;
import com.gresk.modules.review.infrastructure.web.request.UpdateReviewRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Post-concert review endpoints")
public class ReviewController {

    private final TransactionalSubmitReviewService submitReviewService;
    private final TransactionalUpdateReviewService updateReviewService;
    private final GetUserReviewsUseCase            getUserReviewsUseCase;
    private final GetEventRatingStatsUseCase       getEventRatingStatsUseCase;
    private final GetEventReviewsUseCase           getEventReviewsUseCase;
    private final ReviewResponseMapper             mapper;
    private final EventRatingStatsResponseMapper   statsMapper;

    // ── POST /api/v1/reviews ─────────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Submit a post-concert review")
    @ApiResponse(responseCode = "201", description = "Review submitted and points awarded")
    @ApiResponse(responseCode = "403", description = "Ticket does not belong to this user")
    @ApiResponse(responseCode = "404", description = "Ticket not found")
    @ApiResponse(responseCode = "409", description = "Review already exists for this ticket")
    @ApiResponse(responseCode = "422", description = "Invalid rating value")
    public ResponseEntity<ReviewResponse> submit(
            @Valid @RequestBody SubmitReviewRequest request,
            @AuthenticationPrincipal String userId) {

        SubmitReviewCommand command = new SubmitReviewCommand(
                userId,
                request.ticketId(),
                request.eventId(),
                request.artistRating(),
                request.soundRating(),
                request.ambienceRating(),
                request.venueRating(),
                request.setlistRating(),
                request.comment(),
                request.photoUrl()
        );

        Review review = submitReviewService.execute(command);
        return ResponseEntity.status(201).body(mapper.toResponse(review));
    }

    // ── PUT /api/v1/reviews/{id} ─────────────────────────────────────────────

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Edit an existing review")
    @ApiResponse(responseCode = "200", description = "Review updated")
    @ApiResponse(responseCode = "403", description = "Review does not belong to this user")
    @ApiResponse(responseCode = "404", description = "Review not found")
    @ApiResponse(responseCode = "422", description = "Invalid rating value")
    public ResponseEntity<ReviewResponse> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateReviewRequest request,
            @AuthenticationPrincipal String userId) {

        UpdateReviewCommand command = new UpdateReviewCommand(
                id, userId,
                request.artistRating(),   request.soundRating(),
                request.ambienceRating(), request.venueRating(),
                request.setlistRating(),
                request.comment(), request.photoUrl()
        );

        return ResponseEntity.ok(mapper.toResponse(updateReviewService.execute(command)));
    }

    // ── GET /api/v1/reviews/users/me ─────────────────────────────────────────

    @GetMapping("/users/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List authenticated user's reviews")
    @ApiResponse(responseCode = "200", description = "List of reviews")
    public ResponseEntity<List<ReviewResponse>> listMyReviews(
            @AuthenticationPrincipal String userId) {

        List<ReviewResponse> responses = getUserReviewsUseCase
                .execute(new GetUserReviewsQuery(userId))
                .stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    // ── GET /api/v1/reviews/events/{eventId} ────────────────────────────────

    @GetMapping("/events/{eventId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all reviews for an event")
    @ApiResponse(responseCode = "200", description = "List of reviews for the event")
    public ResponseEntity<List<ReviewResponse>> listEventReviews(
            @PathVariable String eventId) {

        List<ReviewResponse> responses = getEventReviewsUseCase
                .execute(eventId)
                .stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    // ── GET /api/v1/reviews/events/{eventId}/stats ───────────────────────────

    @GetMapping("/events/{eventId}/stats")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get community rating stats for an event")
    @ApiResponse(responseCode = "200", description = "Community rating averages")
    @ApiResponse(responseCode = "404", description = "Event not found")
    public ResponseEntity<EventRatingStatsResponse> getEventStats(
            @PathVariable String eventId) {

        return ResponseEntity.ok(
                statsMapper.toResponse(
                        getEventRatingStatsUseCase.execute(
                                new GetEventRatingStatsQuery(eventId))));
    }
}
