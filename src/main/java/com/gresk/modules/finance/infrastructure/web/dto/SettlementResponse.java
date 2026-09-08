package com.gresk.modules.finance.infrastructure.web.dto;

import com.gresk.modules.finance.domain.model.valueobject.SettlementDealType;

import java.math.BigDecimal;
import java.time.Instant;

public record SettlementResponse(
        String id,
        String settlementAgreementId,
        String linkedEventId,
        String status,
        SettlementDealType dealType,
        String currency,
        BigDecimal grossBoxOffice,
        BigDecimal netBoxOffice,
        BigDecimal ticketingCommissionDeducted,
        BigDecimal guaranteedComponent,
        BigDecimal percentageComponent,
        BigDecimal artistPayableAmount,
        Instant calculatedAt,
        Instant approvedAt
) {
}
