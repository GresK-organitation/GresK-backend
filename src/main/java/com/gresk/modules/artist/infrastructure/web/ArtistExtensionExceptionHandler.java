package com.gresk.modules.artist.infrastructure.web;

import com.gresk.modules.artist.domain.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ArtistExtensionExceptionHandler {

    @ExceptionHandler(EpkAssetNotFoundException.class)
    ResponseEntity<Map<String, String>> handleEpkAssetNotFound(EpkAssetNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(EpkAssetVersionNotFoundException.class)
    ResponseEntity<Map<String, String>> handleEpkAssetVersionNotFound(EpkAssetVersionNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(EpkAssetArchivedException.class)
    ResponseEntity<Map<String, String>> handleEpkAssetArchived(EpkAssetArchivedException ex) {
        return ResponseEntity.status(409).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(EpkShareLinkNotFoundException.class)
    ResponseEntity<Map<String, String>> handleShareLinkNotFound(EpkShareLinkNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(EpkShareLinkNotOwnedException.class)
    ResponseEntity<Map<String, String>> handleShareLinkNotOwned(EpkShareLinkNotOwnedException ex) {
        return ResponseEntity.status(403).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler({EpkShareLinkExpiredException.class, EpkShareLinkRevokedException.class, EpkShareLinkNotUsableException.class})
    ResponseEntity<Map<String, String>> handleShareLinkNotUsable(RuntimeException ex) {
        return ResponseEntity.status(410).body(Map.of("error", ex.getMessage())); // 410 Gone
    }

    @ExceptionHandler(RosterMemberNotFoundException.class)
    ResponseEntity<Map<String, String>> handleRosterMemberNotFound(RosterMemberNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(InvalidBillingDetailsException.class)
    ResponseEntity<Map<String, String>> handleInvalidBillingDetails(InvalidBillingDetailsException ex) {
        return ResponseEntity.status(400).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(BandMemberNotFoundException.class)
    ResponseEntity<Map<String, String>> handleBandMemberNotFound(BandMemberNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(InvalidIdentityDocumentException.class)
    ResponseEntity<Map<String, String>> handleInvalidIdentityDocument(InvalidIdentityDocumentException ex) {
        return ResponseEntity.status(400).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(DocumentExpiryAlertNotFoundException.class)
    ResponseEntity<Map<String, String>> handleAlertNotFound(DocumentExpiryAlertNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(DocumentExpiryAlertNotOwnedException.class)
    ResponseEntity<Map<String, String>> handleAlertNotOwned(DocumentExpiryAlertNotOwnedException ex) {
        return ResponseEntity.status(403).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ArtistTractionNotFoundException.class)
    ResponseEntity<Map<String, String>> handleTractionNotFound(ArtistTractionNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }
}
