package com.gresk.modules.show.infrastructure.web;

import com.gresk.modules.show.domain.exception.IncompleteShowException;
import com.gresk.modules.show.domain.exception.InvalidShowStatusTransitionException;
import com.gresk.modules.show.domain.exception.ShowAlreadySettledException;
import com.gresk.modules.show.domain.exception.ShowNotFoundException;
import com.gresk.modules.show.domain.exception.ShowNotOwnedException;
import com.gresk.modules.show.domain.exception.VenueCapacityExceededException;
import com.gresk.modules.show.domain.exception.VenueHoldExpiredException;
import com.gresk.modules.venue.domain.exception.CapacityConfigurationNotFoundException;
import com.gresk.modules.venue.domain.exception.VenueNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ShowExceptionHandler {

    @ExceptionHandler(ShowNotFoundException.class)
    ResponseEntity<Map<String, String>> handleNotFound(ShowNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ShowNotOwnedException.class)
    ResponseEntity<Map<String, String>> handleNotOwned(ShowNotOwnedException ex) {
        return ResponseEntity.status(403).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(InvalidShowStatusTransitionException.class)
    ResponseEntity<Map<String, String>> handleInvalidTransition(InvalidShowStatusTransitionException ex) {
        return ResponseEntity.status(422).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(IncompleteShowException.class)
    ResponseEntity<Map<String, String>> handleIncomplete(IncompleteShowException ex) {
        return ResponseEntity.status(422).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(VenueHoldExpiredException.class)
    ResponseEntity<Map<String, String>> handleHoldExpired(VenueHoldExpiredException ex) {
        return ResponseEntity.status(409).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(VenueCapacityExceededException.class)
    ResponseEntity<Map<String, String>> handleCapacityExceeded(VenueCapacityExceededException ex) {
        return ResponseEntity.status(422).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ShowAlreadySettledException.class)
    ResponseEntity<Map<String, String>> handleAlreadySettled(ShowAlreadySettledException ex) {
        return ResponseEntity.status(409).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(VenueNotFoundException.class)
    ResponseEntity<Map<String, String>> handleVenueNotFound(VenueNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(CapacityConfigurationNotFoundException.class)
    ResponseEntity<Map<String, String>> handleCapacityConfigNotFound(CapacityConfigurationNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }
}
