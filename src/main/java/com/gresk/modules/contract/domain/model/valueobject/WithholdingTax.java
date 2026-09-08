package com.gresk.modules.contract.domain.model.valueobject;

import java.math.BigDecimal;

/**
 * Retención fiscal aplicable al caché. taxBase suele coincidir con feeAmount salvo
 * pacto de "neto de impuestos" (grossing-up), habitual en artistas internacionales.
 */
public record WithholdingTax(
        WithholdingTaxType type,
        BigDecimal          ratePercentage,
        BigDecimal          taxBase,
        BigDecimal          withheldAmount,
        String              exemptionReason
) {
    public WithholdingTax {
        if (type != WithholdingTaxType.NONE && ratePercentage != null) {
            if (ratePercentage.signum() < 0 || ratePercentage.compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new IllegalArgumentException("ratePercentage must be between 0 and 100");
            }
        }
    }

    public static WithholdingTax none() {
        return new WithholdingTax(WithholdingTaxType.NONE, BigDecimal.ZERO, null, null, null);
    }
}
