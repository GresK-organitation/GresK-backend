package com.gresk.modules.email.infrastructure.web;

import com.gresk.modules.email.domain.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice(assignableTypes = {
        EmailPanelController.class,
        EmailRiderController.class,
        DraftReplyController.class,
        GmailOAuthController.class
})
public class EmailExceptionHandler {

    @ExceptionHandler({EmailMessageNotFoundException.class,
                       DraftReplyNotFoundException.class,
                       RiderVersionNotFoundException.class,
                       EventNotOwnedException.class})
    ResponseEntity<Map<String, String>> handleNotFound(RuntimeException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ForbiddenEmailOperationException.class)
    ResponseEntity<Map<String, String>> handleForbidden(ForbiddenEmailOperationException ex) {
        return ResponseEntity.status(403).body(Map.of("error", ex.getMessage()));
    }

    /** Transición de estado inválida (p. ej. aprobar un borrador ya enviado) → 409. */
    @ExceptionHandler(InvalidDraftReplyStatusException.class)
    ResponseEntity<Map<String, String>> handleConflict(InvalidDraftReplyStatusException ex) {
        return ResponseEntity.status(409).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(400).body(Map.of("error", ex.getMessage()));
    }
}
