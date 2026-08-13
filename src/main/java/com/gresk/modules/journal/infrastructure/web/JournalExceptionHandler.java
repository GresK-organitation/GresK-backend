package com.gresk.modules.journal.infrastructure.web;

import com.gresk.modules.journal.domain.exception.InvalidApproxDateException;
import com.gresk.modules.journal.domain.exception.InvalidJournalEntryException;
import com.gresk.modules.journal.domain.exception.InvalidRatingCriterionException;
import com.gresk.modules.journal.domain.exception.JournalEntryForbiddenException;
import com.gresk.modules.journal.domain.exception.JournalEntryNotFoundException;
import com.gresk.modules.journal.domain.exception.TooManyMediaItemsException;
import com.gresk.shared.domain.exception.VideoStorageException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class JournalExceptionHandler {

    @ExceptionHandler(JournalEntryNotFoundException.class)
    ResponseEntity<Map<String, String>> handleNotFound(JournalEntryNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(JournalEntryForbiddenException.class)
    ResponseEntity<Map<String, String>> handleForbidden(JournalEntryForbiddenException ex) {
        return ResponseEntity.status(403).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler({
            InvalidJournalEntryException.class,
            InvalidRatingCriterionException.class,
            InvalidApproxDateException.class,
            TooManyMediaItemsException.class
    })
    ResponseEntity<Map<String, String>> handleInvalid(RuntimeException ex) {
        return ResponseEntity.status(422).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(VideoStorageException.class)
    ResponseEntity<Map<String, String>> handleVideoStorage(VideoStorageException ex) {
        return ResponseEntity.status(422).body(Map.of("error", ex.getMessage()));
    }
}
