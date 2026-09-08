package com.gresk.modules.finance.domain.service;

import com.gresk.modules.finance.domain.model.valueobject.DealTerms;
import com.gresk.modules.finance.domain.model.valueobject.SettlementBreakdown;
import com.gresk.shared.domain.valueobject.Money;

/** Calcula el reparto de taquilla según el tipo de acuerdo pactado. Lógica pura, sin dependencias de infraestructura. */
public final class SettlementCalculator {

    private SettlementCalculator() {
    }

    public static SettlementBreakdown calculate(DealTerms dealTerms, Money grossBoxOffice, Money ticketingCommission) {
        Money netBoxOffice = grossBoxOffice.subtract(ticketingCommission);
        Money guaranteedComponent = null;
        Money percentageComponent = null;
        Money finalAmount;

        switch (dealTerms.type()) {
            case FLAT_FEE -> {
                guaranteedComponent = dealTerms.guaranteedAmount();
                finalAmount = guaranteedComponent;
            }
            case VERSUS -> {
                guaranteedComponent = dealTerms.guaranteedAmount();
                percentageComponent = netBoxOffice.multiply(dealTerms.artistPercentage().movePointLeft(2));
                finalAmount = Money.max(guaranteedComponent, percentageComponent);
            }
            case DOOR_SPLIT -> {
                percentageComponent = netBoxOffice.multiply(dealTerms.artistPercentage().movePointLeft(2));
                finalAmount = percentageComponent;
            }
            case GUARANTEED_MIN_PLUS_PERCENTAGE -> {
                guaranteedComponent = dealTerms.guaranteedAmount();
                Money excess = netBoxOffice.isGreaterThan(dealTerms.revenueThreshold())
                        ? netBoxOffice.subtract(dealTerms.revenueThreshold())
                        : Money.zero(netBoxOffice.currency());
                percentageComponent = excess.multiply(dealTerms.artistPercentage().movePointLeft(2));
                finalAmount = guaranteedComponent.add(percentageComponent);
            }
            default -> throw new IllegalStateException("Unhandled deal type: " + dealTerms.type());
        }

        return new SettlementBreakdown(dealTerms.type(), grossBoxOffice, netBoxOffice, ticketingCommission,
                guaranteedComponent, percentageComponent, finalAmount);
    }
}
