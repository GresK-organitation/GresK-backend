package com.gresk.modules.review.infrastructure.web;

import com.gresk.modules.review.domain.exception.InvalidRatingException;
import com.gresk.modules.review.domain.exception.ReviewAlreadyExistsException;
import com.gresk.modules.review.domain.exception.ReviewAlreadyLikedException;
import com.gresk.modules.review.domain.exception.ReviewForbiddenException;
import com.gresk.modules.review.domain.exception.ReviewNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ReviewExceptionHandler {

    @ExceptionHandler(ReviewNotFoundException.class)
    ResponseEntity<Map<String, String>> handleNotFound(ReviewNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ReviewAlreadyExistsException.class)
    ResponseEntity<Map<String, String>> handleConflict(ReviewAlreadyExistsException ex) {
        return ResponseEntity.status(409).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ReviewForbiddenException.class)
    ResponseEntity<Map<String, String>> handleForbidden(ReviewForbiddenException ex) {
        return ResponseEntity.status(403).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(InvalidRatingException.class)
    ResponseEntity<Map<String, String>> handleInvalidRating(InvalidRatingException ex) {
        return ResponseEntity.status(422).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ReviewAlreadyLikedException.class)
    ResponseEntity<Map<String, String>> handleAlreadyLiked(ReviewAlreadyLikedException ex) {
        return ResponseEntity.status(409).body(Map.of("error", ex.getMessage()));
    }
}
