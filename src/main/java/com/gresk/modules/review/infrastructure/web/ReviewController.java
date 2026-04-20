package com.gresk.modules.review.infrastructure.web;

import com.gresk.modules.review.application.usecase.SubmitReviewCommand;
import com.gresk.modules.review.domain.model.Review;
import com.gresk.modules.review.infrastructure.TransactionalSubmitReviewService;
import com.gresk.modules.review.infrastructure.web.request.SubmitReviewRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Post-concert review endpoints")
public class ReviewController {

    private final TransactionalSubmitReviewService submitReviewService;
    private final ReviewResponseMapper             mapper;

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
}
