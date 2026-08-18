package com.gresk.modules.tendencias.chronicle.infrastructure.web;

import com.gresk.modules.tendencias.chronicle.domain.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ChronicleExceptionHandler {

    @ExceptionHandler({ChronicleNotFoundException.class, FeedSourceNotFoundException.class})
    ResponseEntity<Map<String, String>> handleNotFound(RuntimeException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler({DuplicateFeedSourceException.class, InvalidFeedSourceTransitionException.class,
            InvalidChronicleTransitionException.class})
    ResponseEntity<Map<String, String>> handleConflict(RuntimeException ex) {
        return ResponseEntity.status(409).body(Map.of("error", ex.getMessage()));
    }
}
