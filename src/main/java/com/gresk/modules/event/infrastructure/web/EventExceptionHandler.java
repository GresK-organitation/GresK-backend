package com.gresk.modules.event.infrastructure.web;

import com.gresk.modules.event.domain.exception.EventNotFoundException;
import com.gresk.modules.event.domain.exception.ForbiddenOperationException;
import com.gresk.modules.event.domain.exception.IncompleteEventException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestControllerAdvice
public class EventExceptionHandler {

    @ExceptionHandler(EventNotFoundException.class)
    Mono<ResponseEntity<Map<String, String>>> handleNotFound(EventNotFoundException ex) {
        return Mono.just(ResponseEntity.status(404).body(Map.of("error", ex.getMessage())));
    }

    @ExceptionHandler(IncompleteEventException.class)
    Mono<ResponseEntity<Map<String, String>>> handleIncomplete(IncompleteEventException ex) {
        return Mono.just(ResponseEntity.status(422).body(Map.of("error", ex.getMessage())));
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    Mono<ResponseEntity<Map<String, String>>> handleForbidden(ForbiddenOperationException ex) {
        return Mono.just(ResponseEntity.status(403).body(Map.of("error", ex.getMessage())));
    }
}
