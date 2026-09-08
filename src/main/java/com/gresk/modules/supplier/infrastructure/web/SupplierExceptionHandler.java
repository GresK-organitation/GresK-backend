package com.gresk.modules.supplier.infrastructure.web;

import com.gresk.modules.supplier.domain.exception.SupplierNotFoundException;
import com.gresk.modules.supplier.domain.exception.SupplierNotOwnedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class SupplierExceptionHandler {

    @ExceptionHandler(SupplierNotFoundException.class)
    ResponseEntity<Map<String, String>> handleNotFound(SupplierNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(SupplierNotOwnedException.class)
    ResponseEntity<Map<String, String>> handleNotOwned(SupplierNotOwnedException ex) {
        return ResponseEntity.status(403).body(Map.of("error", ex.getMessage()));
    }
}
