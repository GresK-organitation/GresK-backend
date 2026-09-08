package com.gresk.modules.quotation.infrastructure.web;

import com.gresk.modules.quotation.domain.exception.EventHasNoArtistException;
import com.gresk.modules.quotation.domain.exception.EventQuoteAlreadyExistsException;
import com.gresk.modules.quotation.domain.exception.EventQuoteNotFoundException;
import com.gresk.modules.quotation.domain.exception.QuoteLineNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class QuotationExceptionHandler {

    @ExceptionHandler(EventQuoteNotFoundException.class)
    ResponseEntity<Map<String, String>> handleNotFound(EventQuoteNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(QuoteLineNotFoundException.class)
    ResponseEntity<Map<String, String>> handleLineNotFound(QuoteLineNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(EventQuoteAlreadyExistsException.class)
    ResponseEntity<Map<String, String>> handleAlreadyExists(EventQuoteAlreadyExistsException ex) {
        return ResponseEntity.status(409).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(EventHasNoArtistException.class)
    ResponseEntity<Map<String, String>> handleNoArtist(EventHasNoArtistException ex) {
        return ResponseEntity.status(422).body(Map.of("error", ex.getMessage()));
    }
}
