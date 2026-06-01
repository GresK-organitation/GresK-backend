package com.gresk.modules.rider.infrastructure.web;

import com.gresk.modules.rider.domain.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class RiderExceptionHandler {

    @ExceptionHandler(RiderNotFoundException.class)
    ResponseEntity<Map<String, String>> handleRiderNotFound(RiderNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ChecklistNotFoundException.class)
    ResponseEntity<Map<String, String>> handleChecklistNotFound(ChecklistNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ChecklistEntryNotFoundException.class)
    ResponseEntity<Map<String, String>> handleEntryNotFound(ChecklistEntryNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(RiderAlreadyPublishedException.class)
    ResponseEntity<Map<String, String>> handleAlreadyPublished(RiderAlreadyPublishedException ex) {
        return ResponseEntity.status(409).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(RiderIncompletException.class)
    ResponseEntity<Map<String, String>> handleIncomplete(RiderIncompletException ex) {
        return ResponseEntity.status(422).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(RiderNotOwnedException.class)
    ResponseEntity<Map<String, String>> handleNotOwned(RiderNotOwnedException ex) {
        return ResponseEntity.status(403).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ChecklistAlreadyExistsException.class)
    ResponseEntity<Map<String, String>> handleChecklistExists(ChecklistAlreadyExistsException ex) {
        return ResponseEntity.status(409).body(Map.of("error", ex.getMessage()));
    }
}
