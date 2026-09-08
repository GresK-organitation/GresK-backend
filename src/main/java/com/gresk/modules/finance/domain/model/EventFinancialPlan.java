package com.gresk.modules.finance.domain.model;

import com.gresk.modules.finance.domain.exception.CostLineNotFoundException;
import com.gresk.modules.finance.domain.model.valueobject.CostLine;
import com.gresk.modules.finance.domain.model.valueobject.CostLineId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Presupuesto financiero consolidado de un evento (un único plan por evento, aunque el
 * evento tenga varios contratos/artistas). Base del dashboard de P&L (Fase 1) y de la
 * validación de desviación de {@code SupplierInvoice} (Fase 4).
 */
public final class EventFinancialPlan {

    private static final BigDecimal DEFAULT_DEVIATION_THRESHOLD_PERCENTAGE = BigDecimal.TEN;

    private final EventFinancialPlanId id;
    private final PromoterId           promoterId;
    private final UUID                 linkedEventId;
    private final Instant              createdAt;

    private BigDecimal      deviationThresholdPercentage;
    private List<CostLine>  costLines;
    private Instant         updatedAt;

    private EventFinancialPlan(EventFinancialPlanId id, PromoterId promoterId, UUID linkedEventId,
                                BigDecimal deviationThresholdPercentage, List<CostLine> costLines,
                                Instant createdAt, Instant updatedAt) {
        this.id                           = id;
        this.promoterId                   = promoterId;
        this.linkedEventId                = linkedEventId;
        this.deviationThresholdPercentage = deviationThresholdPercentage;
        this.costLines                    = costLines != null ? new ArrayList<>(costLines) : new ArrayList<>();
        this.createdAt                    = createdAt;
        this.updatedAt                    = updatedAt;
    }

    public static EventFinancialPlan create(PromoterId promoterId, UUID linkedEventId,
                                             BigDecimal deviationThresholdPercentage) {
        Instant now = Instant.now();
        return new EventFinancialPlan(
                EventFinancialPlanId.generate(), promoterId, linkedEventId,
                deviationThresholdPercentage != null ? deviationThresholdPercentage : DEFAULT_DEVIATION_THRESHOLD_PERCENTAGE,
                List.of(), now, now);
    }

    public static EventFinancialPlan reconstitute(EventFinancialPlanId id, PromoterId promoterId, UUID linkedEventId,
                                                   BigDecimal deviationThresholdPercentage, List<CostLine> costLines,
                                                   Instant createdAt, Instant updatedAt) {
        return new EventFinancialPlan(id, promoterId, linkedEventId, deviationThresholdPercentage,
                costLines, createdAt, updatedAt);
    }

    public void addCostLine(CostLine costLine) {
        this.costLines.add(costLine);
        this.updatedAt = Instant.now();
    }

    public void updateCostLine(CostLineId costLineId, CostLine updated) {
        int index = indexOf(costLineId);
        this.costLines.set(index, updated);
        this.updatedAt = Instant.now();
    }

    public void removeCostLine(CostLineId costLineId) {
        int index = indexOf(costLineId);
        this.costLines.remove(index);
        this.updatedAt = Instant.now();
    }

    private int indexOf(CostLineId costLineId) {
        for (int i = 0; i < costLines.size(); i++) {
            if (costLines.get(i).id().equals(costLineId)) return i;
        }
        throw new CostLineNotFoundException(costLineId.toString());
    }

    public EventFinancialPlanId getId()                             { return id; }
    public PromoterId           getPromoterId()                     { return promoterId; }
    public UUID                 getLinkedEventId()                  { return linkedEventId; }
    public BigDecimal           getDeviationThresholdPercentage()    { return deviationThresholdPercentage; }
    public List<CostLine>       getCostLines()                      { return List.copyOf(costLines); }
    public Instant              getCreatedAt()                      { return createdAt; }
    public Instant              getUpdatedAt()                      { return updatedAt; }
}
