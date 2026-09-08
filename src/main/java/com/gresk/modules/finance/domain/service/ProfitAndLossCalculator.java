package com.gresk.modules.finance.domain.service;

import com.gresk.modules.finance.domain.model.EventFinancialPlan;
import com.gresk.modules.finance.domain.model.valueobject.CostCategory;
import com.gresk.modules.finance.domain.model.valueobject.CostLine;
import com.gresk.modules.finance.domain.model.valueobject.CostSubcategory;
import com.gresk.modules.finance.domain.model.valueobject.EventPnLDashboard;
import com.gresk.modules.finance.domain.model.valueobject.EventRevenueSnapshot;
import com.gresk.shared.domain.valueobject.Money;

import java.util.EnumMap;
import java.util.Map;

/**
 * Calcula el desglose de P&L de un evento al vuelo: no persiste nada, combina el
 * presupuesto (EventFinancialPlan) con la taquilla real (EventRevenueSnapshot).
 */
public final class ProfitAndLossCalculator {

    private ProfitAndLossCalculator() {
    }

    public static EventPnLDashboard calculate(EventFinancialPlan plan, EventRevenueSnapshot revenue) {
        String currency = revenue.grossBoxOffice().currency();
        Money fixedTotal = Money.zero(currency);
        Money variableTotal = Money.zero(currency);
        Map<CostSubcategory, Money> breakdown = new EnumMap<>(CostSubcategory.class);

        for (CostLine line : plan.getCostLines()) {
            Money amount = line.resolveAmount(revenue.grossBoxOffice());
            if (line.category() == CostCategory.FIXED) {
                fixedTotal = fixedTotal.add(amount);
            } else {
                variableTotal = variableTotal.add(amount);
            }
            breakdown.merge(line.subcategory(), amount, Money::add);
        }

        var netProfit = revenue.grossBoxOffice().amount()
                .subtract(fixedTotal.amount())
                .subtract(variableTotal.amount());

        return new EventPnLDashboard(
                revenue.grossBoxOffice(), fixedTotal, variableTotal,
                netProfit, currency, breakdown, revenue.ticketsSold());
    }
}
