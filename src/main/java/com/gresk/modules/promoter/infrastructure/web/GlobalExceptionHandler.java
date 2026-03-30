package com.gresk.modules.promoter.infrastructure.web;

import com.gresk.modules.promoter.domain.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PromoterNotFoundException.class)
    Mono<ResponseEntity<Map<String, String>>> handleNotFound(PromoterNotFoundException ex) {
        return Mono.just(ResponseEntity.status(404).body(Map.of("error", ex.getMessage())));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    Mono<ResponseEntity<Map<String, String>>> handleConflict(EmailAlreadyExistsException ex) {
        return Mono.just(ResponseEntity.status(409).body(Map.of("error", ex.getMessage())));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    Mono<ResponseEntity<Map<String, String>>> handleUnauthorized(InvalidCredentialsException ex) {
        return Mono.just(ResponseEntity.status(401).body(Map.of("error", ex.getMessage())));
    }

    @ExceptionHandler(InvalidPromoterNameException.class)
    Mono<ResponseEntity<Map<String, String>>> handleInvalidName(InvalidPromoterNameException ex) {
        return Mono.just(ResponseEntity.status(400).body(Map.of("error", ex.getMessage())));
    }

    @ExceptionHandler(InvalidGenreException.class)
    Mono<ResponseEntity<Map<String, String>>> handleInvalidGenre(InvalidGenreException ex) {
        return Mono.just(ResponseEntity.status(400).body(Map.of("error", ex.getMessage())));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    Mono<ResponseEntity<Map<String, String>>> handleValidation(WebExchangeBindException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return Mono.just(ResponseEntity.status(400).body(Map.of("error", message)));
    }
}
