package com.gresk.modules.review.infrastructure.web;

import com.gresk.modules.review.application.port.in.AddReviewLikePort;
import com.gresk.modules.review.application.port.in.RemoveReviewLikePort;
import com.gresk.modules.review.application.port.in.SubmitReviewPort;
import com.gresk.modules.review.application.port.in.UpdateReviewPort;
import com.gresk.modules.review.application.usecase.AddReviewLikeCommand;
import com.gresk.modules.review.application.usecase.GetEventRatingStatsQuery;
import com.gresk.modules.review.application.usecase.GetEventRatingStatsUseCase;
import com.gresk.modules.review.application.usecase.GetEventReviewsUseCase;
import com.gresk.modules.review.application.usecase.GetUserReviewsQuery;
import com.gresk.modules.review.application.usecase.GetUserReviewsUseCase;
import com.gresk.modules.review.application.usecase.RemoveReviewLikeCommand;
import com.gresk.modules.review.application.usecase.SubmitReviewCommand;
import com.gresk.modules.review.application.usecase.UpdateReviewCommand;
import com.gresk.modules.review.domain.model.Review;
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
import org.springframework.web.bind.annotation.DeleteMapping;
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

    private final SubmitReviewPort              submitReviewPort;
    private final UpdateReviewPort              updateReviewPort;
    private final AddReviewLikePort             addReviewLikePort;
    private final RemoveReviewLikePort          removeReviewLikePort;
    private final GetUserReviewsUseCase         getUserReviewsUseCase;
    private final GetEventRatingStatsUseCase    getEventRatingStatsUseCase;
    private final GetEventReviewsUseCase        getEventReviewsUseCase;
    private final ReviewResponseMapper          mapper;
    private final EventRatingStatsResponseMapper statsMapper;

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

        Review review = submitReviewPort.execute(command);
        return ResponseEntity.status(201).body(mapper.toResponse(review, userId));
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

        return ResponseEntity.ok(mapper.toResponse(updateReviewPort.execute(command), userId));
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
            @PathVariable String eventId,
            @AuthenticationPrincipal String userId) {

        List<ReviewResponse> responses = getEventReviewsUseCase
                .execute(eventId, userId)
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

    // ── POST /api/v1/reviews/{reviewId}/likes ───────────────────────────────

    @PostMapping("/{reviewId}/likes")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Like a review")
    @ApiResponse(responseCode = "200", description = "Like added")
    @ApiResponse(responseCode = "404", description = "Review not found")
    @ApiResponse(responseCode = "409", description = "User has already liked this review")
    public ResponseEntity<ReviewResponse> addLike(
            @PathVariable String reviewId,
            @AuthenticationPrincipal String userId) {

        Review review = addReviewLikePort.execute(new AddReviewLikeCommand(reviewId, userId));
        return ResponseEntity.ok(mapper.toResponse(review, userId));
    }

    // ── DELETE /api/v1/reviews/{reviewId}/likes ─────────────────────────────

    @DeleteMapping("/{reviewId}/likes")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Remove a like from a review")
    @ApiResponse(responseCode = "200", description = "Like removed")
    @ApiResponse(responseCode = "404", description = "Review not found")
    public ResponseEntity<ReviewResponse> removeLike(
            @PathVariable String reviewId,
            @AuthenticationPrincipal String userId) {

        Review review = removeReviewLikePort.execute(new RemoveReviewLikeCommand(reviewId, userId));
        return ResponseEntity.ok(mapper.toResponse(review, userId));
    }
}
