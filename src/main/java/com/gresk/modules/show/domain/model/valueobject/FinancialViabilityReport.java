package com.gresk.modules.show.domain.model.valueobject;

import java.math.BigDecimal;

/**
 * Salida del simulador de viabilidad (P&amp;L Borrador): cuántas entradas hacen falta para
 * no perder dinero y qué beneficio se proyecta con el supuesto de venta introducido.
 */
public record FinancialViabilityReport(
        BigDecimal totalFixedCosts,
        BigDecimal variableCostPerAttendee,
        BigDecimal contributionMarginPerTicket,
        Integer breakEvenAttendees,           // null si el margen por entrada no es positivo: nunca hay break-even
        BigDecimal breakEvenPercentOfCapacity, // null si breakEvenAttendees es null
        int projectedAttendance,
        BigDecimal projectedRevenue,
        BigDecimal projectedProfit,
        BigDecimal marginOfSafetyPercent,      // null si projectedAttendance es 0
        boolean viable                          // breakEvenAttendees alcanzable dentro del aforo del venue
) {
}
