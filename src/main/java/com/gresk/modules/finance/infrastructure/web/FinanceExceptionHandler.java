package com.gresk.modules.finance.infrastructure.web;

import com.gresk.modules.finance.domain.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class FinanceExceptionHandler {

    @ExceptionHandler({
            EventFinancialPlanNotFoundException.class,
            CostLineNotFoundException.class,
            EventNotFoundException.class,
            SettlementAgreementNotFoundException.class,
            SettlementNotFoundException.class,
            PaymentInstallmentNotFoundException.class,
            InvoiceNotFoundException.class,
            SupplierInvoiceNotFoundException.class
    })
    ResponseEntity<Map<String, String>> handleNotFound(RuntimeException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(FinanceResourceNotOwnedException.class)
    ResponseEntity<Map<String, String>> handleNotOwned(FinanceResourceNotOwnedException ex) {
        return ResponseEntity.status(403).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler({
            EventFinancialPlanAlreadyExistsException.class,
            SettlementAgreementAlreadyActiveException.class
    })
    ResponseEntity<Map<String, String>> handleAlreadyExists(RuntimeException ex) {
        return ResponseEntity.status(409).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler({
            LinkedContractNotEligibleException.class,
            InvalidSettlementStatusTransitionException.class,
            InvalidInstallmentStatusTransitionException.class,
            InvalidInvoiceStatusTransitionException.class,
            InvalidSupplierInvoiceStatusTransitionException.class
    })
    ResponseEntity<Map<String, String>> handleUnprocessable(RuntimeException ex) {
        return ResponseEntity.status(422).body(Map.of("error", ex.getMessage()));
    }
}
