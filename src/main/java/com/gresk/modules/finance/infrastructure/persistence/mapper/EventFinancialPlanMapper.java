package com.gresk.modules.finance.infrastructure.persistence.mapper;

import com.gresk.modules.finance.domain.model.EventFinancialPlan;
import com.gresk.modules.finance.domain.model.EventFinancialPlanId;
import com.gresk.modules.finance.domain.model.valueobject.CostCategory;
import com.gresk.modules.finance.domain.model.valueobject.CostLine;
import com.gresk.modules.finance.domain.model.valueobject.CostLineId;
import com.gresk.modules.finance.domain.model.valueobject.CostSubcategory;
import com.gresk.modules.finance.infrastructure.persistence.entity.CostLineEntity;
import com.gresk.modules.finance.infrastructure.persistence.entity.EventFinancialPlanEntity;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Money;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventFinancialPlanMapper {

    public EventFinancialPlan toDomain(EventFinancialPlanEntity e, List<CostLineEntity> costLineEntities) {
        List<CostLine> costLines = costLineEntities.stream().map(this::toDomain).toList();
        return EventFinancialPlan.reconstitute(
                EventFinancialPlanId.of(e.getId()),
                PromoterId.of(e.getPromoterId()),
                e.getLinkedEventId(),
                e.getDeviationThresholdPercentage(),
                costLines,
                e.getCreatedAt(), e.getUpdatedAt());
    }

    public EventFinancialPlanEntity toEntity(EventFinancialPlan plan) {
        return EventFinancialPlanEntity.builder()
                .id(plan.getId().value())
                .promoterId(plan.getPromoterId().value())
                .linkedEventId(plan.getLinkedEventId())
                .deviationThresholdPercentage(plan.getDeviationThresholdPercentage())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .build();
    }

    public List<CostLineEntity> toEntities(EventFinancialPlan plan) {
        return plan.getCostLines().stream().map(cl -> toEntity(cl, plan.getId().value())).toList();
    }

    private CostLine toDomain(CostLineEntity e) {
        Money budgetedAmount = e.getBudgetedAmount() != null
                ? new Money(e.getBudgetedAmount(), e.getCurrency() != null ? e.getCurrency() : "EUR")
                : null;
        return new CostLine(
                CostLineId.of(e.getId()),
                CostCategory.valueOf(e.getCategory()),
                CostSubcategory.valueOf(e.getSubcategory()),
                e.getDescription(),
                budgetedAmount,
                e.getVariablePercentage());
    }

    private CostLineEntity toEntity(CostLine cl, java.util.UUID planId) {
        return CostLineEntity.builder()
                .id(cl.id().value())
                .planId(planId)
                .category(cl.category().name())
                .subcategory(cl.subcategory().name())
                .description(cl.description())
                .budgetedAmount(cl.budgetedAmount() != null ? cl.budgetedAmount().amount() : null)
                .currency(cl.budgetedAmount() != null ? cl.budgetedAmount().currency() : null)
                .variablePercentage(cl.variablePercentage())
                .build();
    }
}
