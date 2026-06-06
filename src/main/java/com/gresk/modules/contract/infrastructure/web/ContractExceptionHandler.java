package com.gresk.modules.contract.infrastructure.web;

import com.gresk.modules.contract.domain.exception.*;
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
}
