package com.gresk.modules.contract.domain.model.valueobject;

import java.math.BigDecimal;
import java.util.List;

public record FinancialTerms(
        BigDecimal        feeAmount,
        String            feeCurrency,
        List<PaymentTerm> paymentTerms,
        WithholdingTax    withholdingTax   // nullable = no evaluada todavía
) {
    public FinancialTerms(BigDecimal feeAmount, String feeCurrency, List<PaymentTerm> paymentTerms) {
        this(feeAmount, feeCurrency, paymentTerms, null);
    }
}
