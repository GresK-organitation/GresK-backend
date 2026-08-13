package com.gresk.modules.curation.infrastructure.web;

import com.gresk.modules.curation.domain.exception.CuratedListForbiddenException;
import com.gresk.modules.curation.domain.exception.CuratedListNotFoundException;
import com.gresk.modules.curation.domain.exception.DuplicateListItemException;
import com.gresk.modules.curation.domain.exception.InvalidCuratedListException;
import com.gresk.modules.curation.domain.exception.ListItemNotFoundException;
import com.gresk.modules.curation.domain.exception.ReferencedEntryNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class CurationExceptionHandler {

    @ExceptionHandler({CuratedListNotFoundException.class, ListItemNotFoundException.class, ReferencedEntryNotFoundException.class})
    ResponseEntity<Map<String, String>> handleNotFound(RuntimeException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(CuratedListForbiddenException.class)
    ResponseEntity<Map<String, String>> handleForbidden(CuratedListForbiddenException ex) {
        return ResponseEntity.status(403).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(DuplicateListItemException.class)
    ResponseEntity<Map<String, String>> handleConflict(DuplicateListItemException ex) {
        return ResponseEntity.status(409).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(InvalidCuratedListException.class)
    ResponseEntity<Map<String, String>> handleInvalid(InvalidCuratedListException ex) {
        return ResponseEntity.status(422).body(Map.of("error", ex.getMessage()));
    }
}
