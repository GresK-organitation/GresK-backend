package com.gresk.modules.finance.domain.service;

import com.gresk.modules.finance.domain.model.valueobject.WithholdingApplication;
import com.gresk.modules.finance.domain.model.valueobject.WithholdingKind;
import com.gresk.shared.domain.valueobject.Money;

import java.math.BigDecimal;

/**
 * Calcula la retención fiscal aplicable a un pago. Tasas por defecto orientativas
 * (IRPF/IRNR), sobreescribibles por el promotor — igual de manual/parametrizado que
 * contract.WithholdingTax, no consulta ninguna tabla oficial de Hacienda.
 */
public final class WithholdingCalculator {

    private static final BigDecimal DEFAULT_IRPF_RATE = BigDecimal.valueOf(15);
    private static final BigDecimal DEFAULT_IRNR_RATE  = BigDecimal.valueOf(24);

    private WithholdingCalculator() {
    }

    public static WithholdingApplication apply(Money taxBase, WithholdingKind kind,
                                                 BigDecimal ratePercentageOverride, String exemptionReason) {
        if (kind == WithholdingKind.NONE) {
            return WithholdingApplication.none(taxBase);
        }

        BigDecimal rate = ratePercentageOverride != null ? ratePercentageOverride : defaultRateFor(kind);
        Money withheld = taxBase.multiply(rate.movePointLeft(2));
        return new WithholdingApplication(kind, rate, taxBase, withheld, exemptionReason);
    }

    private static BigDecimal defaultRateFor(WithholdingKind kind) {
        return switch (kind) {
            case IRPF_DOMESTIC -> DEFAULT_IRPF_RATE;
            case IRNR_NON_RESIDENT -> DEFAULT_IRNR_RATE;
            case EU_REVERSE_CHARGE, NONE -> BigDecimal.ZERO;
        };
    }
}
