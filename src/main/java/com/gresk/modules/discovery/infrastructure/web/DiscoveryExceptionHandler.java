package com.gresk.modules.discovery.infrastructure.web;

import com.gresk.modules.discovery.domain.exception.ArtistDiscoveryProfileNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class DiscoveryExceptionHandler {

    @ExceptionHandler(ArtistDiscoveryProfileNotFoundException.class)
    ResponseEntity<Map<String, String>> handleNotFound(ArtistDiscoveryProfileNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }
}
