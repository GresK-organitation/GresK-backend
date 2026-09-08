package com.gresk.modules.show.domain.model.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Input inmutable del simulador de viabilidad: la lista de costes estimados y los supuestos
 * de ingreso. {@link #calculateBreakEven(int)} es pura -- sin dependencias de infraestructura --
 * de forma que puede recalcularse en caliente cada vez que el usuario ajusta un coste en el
 * formulario, tal y como pide el "P&amp;L Borrador".
 */
public record FinancialSimulation(List<CostLineItem> costLineItems, RevenueAssumptions revenueAssumptions) {

    private static final int SCALE = 2;

    public FinancialSimulation {
        costLineItems = costLineItems == null ? List.of() : List.copyOf(costLineItems);
        if (revenueAssumptions == null) {
            throw new IllegalArgumentException("FinancialSimulation revenueAssumptions must not be null");
        }
    }

    public BigDecimal totalFixedCosts() {
        return sumWhere(CostNature.FIXED);
    }

    public BigDecimal variableCostPerAttendee() {
        return sumWhere(CostNature.VARIABLE_PER_ATTENDEE);
    }

    private BigDecimal sumWhere(CostNature nature) {
        return costLineItems.stream()
                .filter(item -> item.nature() == nature)
                .map(CostLineItem::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcula el break-even (nº de entradas necesarias para cubrir costes) y la proyección de
     * beneficio para el aforo elegido, dado el supuesto de venta media configurado.
     *
     * @param venueCapacity aforo total confirmado para el show
     */
    public FinancialViabilityReport calculateBreakEven(int venueCapacity) {
        BigDecimal fixedCosts    = totalFixedCosts();
        BigDecimal variableCost  = variableCostPerAttendee();
        BigDecimal ticketPrice   = revenueAssumptions.avgTicketPrice();
        BigDecimal contribution  = ticketPrice.subtract(variableCost);

        Integer breakEvenAttendees = null;
        BigDecimal breakEvenPercent = null;
        boolean viable = false;

        if (contribution.signum() > 0) {
            breakEvenAttendees = fixedCosts
                    .divide(contribution, 0, RoundingMode.CEILING)
                    .intValue();
            breakEvenPercent = percentOf(breakEvenAttendees, venueCapacity);
            viable = breakEvenAttendees <= venueCapacity;
        }

        int projectedAttendance = BigDecimal.valueOf(venueCapacity)
                .multiply(revenueAssumptions.expectedSelloutPercent().asFraction())
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();

        BigDecimal projectedRevenue = ticketPrice.multiply(BigDecimal.valueOf(projectedAttendance));
        BigDecimal projectedVariableCosts = variableCost.multiply(BigDecimal.valueOf(projectedAttendance));
        BigDecimal projectedProfit = projectedRevenue.subtract(fixedCosts).subtract(projectedVariableCosts)
                .setScale(SCALE, RoundingMode.HALF_UP);

        BigDecimal marginOfSafetyPercent = null;
        if (projectedAttendance > 0 && breakEvenAttendees != null) {
            marginOfSafetyPercent = BigDecimal.valueOf(projectedAttendance - breakEvenAttendees)
                    .divide(BigDecimal.valueOf(projectedAttendance), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(SCALE, RoundingMode.HALF_UP);
        }

        return new FinancialViabilityReport(
                fixedCosts.setScale(SCALE, RoundingMode.HALF_UP),
                variableCost.setScale(SCALE, RoundingMode.HALF_UP),
                contribution.setScale(SCALE, RoundingMode.HALF_UP),
                breakEvenAttendees,
                breakEvenPercent,
                projectedAttendance,
                projectedRevenue.setScale(SCALE, RoundingMode.HALF_UP),
                projectedProfit,
                marginOfSafetyPercent,
                viable
        );
    }

    private static BigDecimal percentOf(int part, int total) {
        if (total <= 0) return null;
        return BigDecimal.valueOf(part)
                .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(SCALE, RoundingMode.HALF_UP);
    }
}
