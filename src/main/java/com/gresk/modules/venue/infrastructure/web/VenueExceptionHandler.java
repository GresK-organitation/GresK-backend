package com.gresk.modules.venue.infrastructure.web;

import com.gresk.modules.venue.domain.exception.CapacityConfigurationNotFoundException;
import com.gresk.modules.venue.domain.exception.DuplicateCapacityConfigurationException;
import com.gresk.modules.venue.domain.exception.VenueNotFoundException;
import com.gresk.modules.venue.domain.exception.VenueNotOwnedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class VenueExceptionHandler {

    @ExceptionHandler(VenueNotFoundException.class)
    ResponseEntity<Map<String, String>> handleNotFound(VenueNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(VenueNotOwnedException.class)
    ResponseEntity<Map<String, String>> handleNotOwned(VenueNotOwnedException ex) {
        return ResponseEntity.status(403).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(DuplicateCapacityConfigurationException.class)
    ResponseEntity<Map<String, String>> handleDuplicateCapacity(DuplicateCapacityConfigurationException ex) {
        return ResponseEntity.status(409).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(CapacityConfigurationNotFoundException.class)
    ResponseEntity<Map<String, String>> handleCapacityNotFound(CapacityConfigurationNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }
}
