package com.gresk.modules.finance.infrastructure.web;

import com.gresk.modules.finance.application.command.AddCostLineCommand;
import com.gresk.modules.finance.application.command.CreateEventFinancialPlanCommand;
import com.gresk.modules.finance.application.command.RemoveCostLineCommand;
import com.gresk.modules.finance.application.command.UpdateCostLineCommand;
import com.gresk.modules.finance.application.query.GetEventPnLDashboardQuery;
import com.gresk.modules.finance.application.usecase.AddCostLineUseCase;
import com.gresk.modules.finance.application.usecase.CreateEventFinancialPlanUseCase;
import com.gresk.modules.finance.application.usecase.GetEventPnLDashboardUseCase;
import com.gresk.modules.finance.application.usecase.RemoveCostLineUseCase;
import com.gresk.modules.finance.application.usecase.UpdateCostLineUseCase;
import com.gresk.modules.finance.infrastructure.web.dto.CostLineRequest;
import com.gresk.modules.finance.infrastructure.web.dto.CreateEventFinancialPlanRequest;
import com.gresk.modules.finance.infrastructure.web.dto.EventFinancialPlanResponse;
import com.gresk.modules.finance.infrastructure.web.dto.EventPnLDashboardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/finance")
@RequiredArgsConstructor
public class EventFinancialPlanController {

    private final CreateEventFinancialPlanUseCase createUseCase;
    private final AddCostLineUseCase              addCostLineUseCase;
    private final UpdateCostLineUseCase           updateCostLineUseCase;
    private final RemoveCostLineUseCase           removeCostLineUseCase;
    private final GetEventPnLDashboardUseCase     pnlUseCase;
    private final FinanceResponseMapper           mapper;

    @PostMapping("/events/{eventId}/plan")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<EventFinancialPlanResponse> createPlan(
            @PathVariable String eventId,
            @RequestBody(required = false) CreateEventFinancialPlanRequest request,
            @AuthenticationPrincipal String promoterId) {

        var deviationThreshold = request != null ? request.deviationThresholdPercentage() : null;
        var plan = createUseCase.execute(new CreateEventFinancialPlanCommand(promoterId, eventId, deviationThreshold));

        return ResponseEntity
                .created(URI.create("/api/v1/finance/plans/" + plan.getId()))
                .body(mapper.toResponse(plan));
    }

    @PostMapping("/plans/{planId}/cost-lines")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<EventFinancialPlanResponse> addCostLine(
            @PathVariable String planId,
            @RequestBody CostLineRequest request,
            @AuthenticationPrincipal String promoterId) {

        var plan = addCostLineUseCase.execute(new AddCostLineCommand(
                planId, promoterId, request.category(), request.subcategory(),
                request.description(), request.budgetedAmount(), request.variablePercentage(), request.currency()));

        return ResponseEntity.ok(mapper.toResponse(plan));
    }

    @PutMapping("/plans/{planId}/cost-lines/{costLineId}")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<EventFinancialPlanResponse> updateCostLine(
            @PathVariable String planId,
            @PathVariable String costLineId,
            @RequestBody CostLineRequest request,
            @AuthenticationPrincipal String promoterId) {

        var plan = updateCostLineUseCase.execute(new UpdateCostLineCommand(
                planId, costLineId, promoterId, request.category(), request.subcategory(),
                request.description(), request.budgetedAmount(), request.variablePercentage(), request.currency()));

        return ResponseEntity.ok(mapper.toResponse(plan));
    }

    @DeleteMapping("/plans/{planId}/cost-lines/{costLineId}")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<Void> removeCostLine(
            @PathVariable String planId,
            @PathVariable String costLineId,
            @AuthenticationPrincipal String promoterId) {

        removeCostLineUseCase.execute(new RemoveCostLineCommand(planId, costLineId, promoterId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/events/{eventId}/pnl")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<EventPnLDashboardResponse> getPnLDashboard(
            @PathVariable String eventId,
            @AuthenticationPrincipal String promoterId) {

        var dashboard = pnlUseCase.execute(new GetEventPnLDashboardQuery(eventId, promoterId));
        return ResponseEntity.ok(mapper.toResponse(dashboard));
    }
}
