package com.gresk.modules.finance.domain.model.valueobject;

import com.gresk.shared.domain.valueobject.Money;

/** Auditoría del cálculo de un Settlement: qué componentes de la fórmula produjeron el importe final. */
public record SettlementBreakdown(
        SettlementDealType dealType,
        Money grossBoxOffice,
        Money netBoxOffice,
        Money ticketingCommissionDeducted,
        Money guaranteedComponent,
        Money percentageComponent,
        Money finalArtistAmount
) {
    public SettlementBreakdown {
        if (dealType == null) throw new IllegalArgumentException("dealType is required");
        if (grossBoxOffice == null) throw new IllegalArgumentException("grossBoxOffice is required");
        if (netBoxOffice == null) throw new IllegalArgumentException("netBoxOffice is required");
        if (finalArtistAmount == null) throw new IllegalArgumentException("finalArtistAmount is required");
    }
}
