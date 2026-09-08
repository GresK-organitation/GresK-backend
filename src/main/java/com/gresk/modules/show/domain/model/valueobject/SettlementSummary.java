package com.gresk.modules.show.domain.model.valueobject;

import java.math.BigDecimal;
import java.time.Instant;

/** Resultado real del show una vez liquidado, para comparar contra el {@code FinancialSimulation} inicial. */
public record SettlementSummary(int actualAttendance, BigDecimal actualRevenue, BigDecimal actualCosts,
                                 BigDecimal netResult, Instant settledAt) {

    public SettlementSummary {
        if (actualAttendance < 0) {
            throw new IllegalArgumentException("SettlementSummary actualAttendance must not be negative");
        }
        if (actualRevenue == null || actualCosts == null) {
            throw new IllegalArgumentException("SettlementSummary actualRevenue/actualCosts must not be null");
        }
        if (settledAt == null) {
            throw new IllegalArgumentException("SettlementSummary settledAt must not be null");
        }
    }

    public static SettlementSummary of(int actualAttendance, BigDecimal actualRevenue, BigDecimal actualCosts) {
        return new SettlementSummary(actualAttendance, actualRevenue, actualCosts,
                actualRevenue.subtract(actualCosts), Instant.now());
    }
}
