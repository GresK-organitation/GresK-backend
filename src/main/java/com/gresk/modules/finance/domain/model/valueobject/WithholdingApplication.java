package com.gresk.modules.finance.domain.model.valueobject;

import com.gresk.shared.domain.valueobject.Money;

import java.math.BigDecimal;

/** Retención fiscal aplicada a un pago concreto (installment). Calcado del shape conceptual de contract.WithholdingTax. */
public record WithholdingApplication(
        WithholdingKind kind,
        BigDecimal ratePercentage,
        Money taxBase,
        Money withheldAmount,
        String exemptionReason
) {
    public WithholdingApplication {
        if (kind == null) throw new IllegalArgumentException("kind is required");
        if (kind != WithholdingKind.NONE && ratePercentage != null
                && (ratePercentage.signum() < 0 || ratePercentage.compareTo(BigDecimal.valueOf(100)) > 0)) {
            throw new IllegalArgumentException("ratePercentage must be between 0 and 100");
        }
    }

    public static WithholdingApplication none(Money taxBase) {
        return new WithholdingApplication(WithholdingKind.NONE, BigDecimal.ZERO, taxBase, Money.zero(taxBase.currency()), null);
    }
}
