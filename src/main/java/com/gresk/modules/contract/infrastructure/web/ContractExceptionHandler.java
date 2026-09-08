package com.gresk.modules.contract.infrastructure.web;

import com.gresk.modules.contract.domain.exception.*;
import com.gresk.modules.contract.infrastructure.template.TemplateRenderingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ContractExceptionHandler {

    @ExceptionHandler(ContractNotFoundException.class)
    ResponseEntity<Map<String, String>> handleNotFound(ContractNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ContractNotOwnedException.class)
    ResponseEntity<Map<String, String>> handleNotOwned(ContractNotOwnedException ex) {
        return ResponseEntity.status(403).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(InvalidContractStatusTransitionException.class)
    ResponseEntity<Map<String, String>> handleInvalidTransition(InvalidContractStatusTransitionException ex) {
        return ResponseEntity.status(422).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ContractAlreadySignedException.class)
    ResponseEntity<Map<String, String>> handleAlreadySigned(ContractAlreadySignedException ex) {
        return ResponseEntity.status(409).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(SignatureEnvelopeNotFoundException.class)
    ResponseEntity<Map<String, String>> handleEnvelopeNotFound(SignatureEnvelopeNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(SignatureEnvelopeAlreadyActiveException.class)
    ResponseEntity<Map<String, String>> handleEnvelopeAlreadyActive(SignatureEnvelopeAlreadyActiveException ex) {
        return ResponseEntity.status(409).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(InvalidSignatureEnvelopeTransitionException.class)
    ResponseEntity<Map<String, String>> handleInvalidEnvelopeTransition(InvalidSignatureEnvelopeTransitionException ex) {
        return ResponseEntity.status(422).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ContractVersionNotFoundException.class)
    ResponseEntity<Map<String, String>> handleVersionNotFound(ContractVersionNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ClauseTemplateNotFoundException.class)
    ResponseEntity<Map<String, String>> handleClauseTemplateNotFound(ClauseTemplateNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ClauseTemplateNotOwnedException.class)
    ResponseEntity<Map<String, String>> handleClauseTemplateNotOwned(ClauseTemplateNotOwnedException ex) {
        return ResponseEntity.status(403).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ContractTemplateNotFoundException.class)
    ResponseEntity<Map<String, String>> handleContractTemplateNotFound(ContractTemplateNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ContractTemplateNotOwnedException.class)
    ResponseEntity<Map<String, String>> handleContractTemplateNotOwned(ContractTemplateNotOwnedException ex) {
        return ResponseEntity.status(403).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(TemplateRenderingException.class)
    ResponseEntity<Map<String, String>> handleTemplateRenderingError(TemplateRenderingException ex) {
        return ResponseEntity.status(500).body(Map.of("error", "Failed to render contract template"));
    }
}
