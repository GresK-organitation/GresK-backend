package com.gresk.shared.infrastructure.web;

import com.gresk.modules.account.domain.exception.AccountAlreadyExistsException;
import com.gresk.modules.account.domain.exception.InvalidAccountCredentialsException;
import com.gresk.modules.promoter.domain.exception.*;
import com.gresk.modules.user.domain.exception.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Identity exceptions
    @ExceptionHandler(AccountAlreadyExistsException.class)
    ResponseEntity<Map<String, String>> handleAccountAlreadyExists(AccountAlreadyExistsException ex) {
        return ResponseEntity.status(409).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(InvalidAccountCredentialsException.class)
    ResponseEntity<Map<String, String>> handleInvalidCredentials(InvalidAccountCredentialsException ex) {
        return ResponseEntity.status(401).body(Map.of("error", ex.getMessage()));
    }

    // User exceptions
    @ExceptionHandler(UserNotFoundException.class)
    ResponseEntity<Map<String, String>> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    // Promoter exceptions
    @ExceptionHandler(PromoterNotFoundException.class)
    ResponseEntity<Map<String, String>> handleNotFound(PromoterNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    ResponseEntity<Map<String, String>> handleConflict(EmailAlreadyExistsException ex) {
        return ResponseEntity.status(409).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    ResponseEntity<Map<String, String>> handlePromoterUnauthorized(InvalidCredentialsException ex) {
        return ResponseEntity.status(401).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(InvalidPromoterNameException.class)
    ResponseEntity<Map<String, String>> handleInvalidName(InvalidPromoterNameException ex) {
        return ResponseEntity.status(400).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(InvalidGenreException.class)
    ResponseEntity<Map<String, String>> handleInvalidGenre(InvalidGenreException ex) {
        return ResponseEntity.status(400).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(400).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(400).body(Map.of("error", message));
    }
}
