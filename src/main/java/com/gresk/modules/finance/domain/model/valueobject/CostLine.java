package com.gresk.modules.finance.domain.model.valueobject;

import com.gresk.shared.domain.valueobject.Money;

import java.math.BigDecimal;

/**
 * Línea de coste de un {@code EventFinancialPlan}. Una línea FIXED se presupuesta como
 * importe cerrado (budgetedAmount); una línea VARIABLE se presupuesta como porcentaje sobre
 * la taquilla bruta (variablePercentage) — por eso ambos campos son mutuamente excluyentes.
 */
public record CostLine(
        CostLineId id,
        CostCategory category,
        CostSubcategory subcategory,
        String description,
        Money budgetedAmount,
        BigDecimal variablePercentage
) {
    public CostLine {
        if (category == null) throw new IllegalArgumentException("category is required");
        if (subcategory == null) throw new IllegalArgumentException("subcategory is required");
        if (category == CostCategory.FIXED && variablePercentage != null) {
            throw new IllegalArgumentException("FIXED cost lines cannot use variablePercentage");
        }
        if (budgetedAmount == null && variablePercentage == null) {
            throw new IllegalArgumentException("either budgetedAmount or variablePercentage is required");
        }
        if (variablePercentage != null
                && (variablePercentage.signum() < 0 || variablePercentage.compareTo(BigDecimal.valueOf(100)) > 0)) {
            throw new IllegalArgumentException("variablePercentage must be between 0 and 100");
        }
    }

    public boolean isPercentageBased() {
        return variablePercentage != null;
    }

    /** Importe efectivo de esta línea dada la taquilla bruta del evento (para líneas VARIABLE). */
    public Money resolveAmount(Money grossBoxOffice) {
        if (!isPercentageBased()) return budgetedAmount;
        return grossBoxOffice.multiply(variablePercentage.movePointLeft(2));
    }
}
