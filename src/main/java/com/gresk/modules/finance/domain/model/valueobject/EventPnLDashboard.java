package com.gresk.modules.finance.domain.model.valueobject;

import com.gresk.shared.domain.valueobject.Money;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Resultado calculado al vuelo por {@code ProfitAndLossCalculator}, no persistido.
 * netProfit es BigDecimal (no Money) porque, a diferencia de un importe cobrado o
 * presupuestado, el resultado neto de un evento puede ser negativo (pérdida) — Money
 * nunca admite un importe negativo.
 */
public record EventPnLDashboard(
        Money grossRevenue,
        Money totalFixedCosts,
        Money totalVariableCosts,
        BigDecimal netProfit,
        String currency,
        Map<CostSubcategory, Money> costBreakdown,
        int ticketsSold
) {
}
