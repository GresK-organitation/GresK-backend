package com.gresk.modules.musicdna.infrastructure.web;

import com.gresk.modules.musicdna.domain.exception.MusicDnaNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class MusicDnaExceptionHandler {

    @ExceptionHandler(MusicDnaNotFoundException.class)
    ResponseEntity<Map<String, String>> handleNotFound(MusicDnaNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }
}
