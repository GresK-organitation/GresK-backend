package com.gresk.modules.finance.infrastructure.web;

import com.gresk.modules.finance.application.command.ApproveSettlementCommand;
import com.gresk.modules.finance.application.command.CalculateSettlementCommand;
import com.gresk.modules.finance.application.command.CreateSettlementAgreementCommand;
import com.gresk.modules.finance.application.command.VoidSettlementAgreementCommand;
import com.gresk.modules.finance.application.command.VoidSettlementCommand;
import com.gresk.modules.finance.application.query.GetSettlementQuery;
import com.gresk.modules.finance.application.query.ListSettlementsQuery;
import com.gresk.modules.finance.application.usecase.ApproveSettlementUseCase;
import com.gresk.modules.finance.application.usecase.CalculateSettlementUseCase;
import com.gresk.modules.finance.application.usecase.CreateSettlementAgreementUseCase;
import com.gresk.modules.finance.application.usecase.GetSettlementUseCase;
import com.gresk.modules.finance.application.usecase.ListSettlementsForPromoterUseCase;
import com.gresk.modules.finance.application.usecase.VoidSettlementAgreementUseCase;
import com.gresk.modules.finance.application.usecase.VoidSettlementUseCase;
import com.gresk.modules.finance.infrastructure.web.dto.CreateSettlementAgreementRequest;
import com.gresk.modules.finance.infrastructure.web.dto.SettlementAgreementResponse;
import com.gresk.modules.finance.infrastructure.web.dto.SettlementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/finance/settlements")
@RequiredArgsConstructor
public class SettlementController {

    private final CreateSettlementAgreementUseCase createAgreementUseCase;
    private final VoidSettlementAgreementUseCase    voidAgreementUseCase;
    private final CalculateSettlementUseCase        calculateUseCase;
    private final ApproveSettlementUseCase          approveUseCase;
    private final VoidSettlementUseCase             voidSettlementUseCase;
    private final GetSettlementUseCase              getUseCase;
    private final ListSettlementsForPromoterUseCase listUseCase;
    private final FinanceResponseMapper             mapper;

    @PostMapping("/agreements")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<SettlementAgreementResponse> createAgreement(
            @RequestBody CreateSettlementAgreementRequest request,
            @AuthenticationPrincipal String promoterId) {

        var agreement = createAgreementUseCase.execute(new CreateSettlementAgreementCommand(
                promoterId, request.linkedContractId(), request.dealType(),
                request.guaranteedAmount(), request.artistPercentage(), request.revenueThreshold()));

        return ResponseEntity
                .created(URI.create("/api/v1/finance/settlements/agreements/" + agreement.getId()))
                .body(mapper.toResponse(agreement));
    }

    @DeleteMapping("/agreements/{agreementId}")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<Void> voidAgreement(
            @PathVariable String agreementId,
            @AuthenticationPrincipal String promoterId) {

        voidAgreementUseCase.execute(new VoidSettlementAgreementCommand(agreementId, promoterId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/agreements/{agreementId}/calculate")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<SettlementResponse> calculate(
            @PathVariable String agreementId,
            @AuthenticationPrincipal String promoterId) {

        var settlement = calculateUseCase.execute(new CalculateSettlementCommand(agreementId, promoterId));
        return ResponseEntity.ok(mapper.toResponse(settlement));
    }

    @PostMapping("/{settlementId}/approve")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<SettlementResponse> approve(
            @PathVariable String settlementId,
            @AuthenticationPrincipal String promoterId) {

        var settlement = approveUseCase.execute(new ApproveSettlementCommand(settlementId, promoterId));
        return ResponseEntity.ok(mapper.toResponse(settlement));
    }

    @PostMapping("/{settlementId}/void")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<Void> voidSettlement(
            @PathVariable String settlementId,
            @AuthenticationPrincipal String promoterId) {

        voidSettlementUseCase.execute(new VoidSettlementCommand(settlementId, promoterId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{settlementId}")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<SettlementResponse> get(
            @PathVariable String settlementId,
            @AuthenticationPrincipal String promoterId) {

        var settlement = getUseCase.execute(new GetSettlementQuery(settlementId, promoterId));
        return ResponseEntity.ok(mapper.toResponse(settlement));
    }

    @GetMapping
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<List<SettlementResponse>> list(@AuthenticationPrincipal String promoterId) {
        var settlements = listUseCase.execute(new ListSettlementsQuery(promoterId));
        return ResponseEntity.ok(settlements.stream().map(mapper::toResponse).toList());
    }
}
