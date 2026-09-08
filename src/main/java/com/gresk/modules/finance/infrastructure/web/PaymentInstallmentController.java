package com.gresk.modules.finance.infrastructure.web;

import com.gresk.modules.finance.application.command.CalculateWithholdingCommand;
import com.gresk.modules.finance.application.command.CancelInstallmentCommand;
import com.gresk.modules.finance.application.command.RecordInstallmentPaymentCommand;
import com.gresk.modules.finance.application.command.ScheduleDepositCommand;
import com.gresk.modules.finance.application.query.GetPaymentScheduleQuery;
import com.gresk.modules.finance.application.usecase.CalculateWithholdingUseCase;
import com.gresk.modules.finance.application.usecase.CancelInstallmentUseCase;
import com.gresk.modules.finance.application.usecase.GetPaymentScheduleUseCase;
import com.gresk.modules.finance.application.usecase.RecordInstallmentPaymentUseCase;
import com.gresk.modules.finance.application.usecase.ScheduleDepositUseCase;
import com.gresk.modules.finance.infrastructure.web.dto.CalculateWithholdingRequest;
import com.gresk.modules.finance.infrastructure.web.dto.PaymentInstallmentResponse;
import com.gresk.modules.finance.infrastructure.web.dto.RecordInstallmentPaymentRequest;
import com.gresk.modules.finance.infrastructure.web.dto.ScheduleDepositRequest;
import com.gresk.modules.finance.infrastructure.web.dto.WithholdingApplicationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/finance/installments")
@RequiredArgsConstructor
public class PaymentInstallmentController {

    private final ScheduleDepositUseCase          scheduleDepositUseCase;
    private final RecordInstallmentPaymentUseCase recordPaymentUseCase;
    private final CancelInstallmentUseCase        cancelUseCase;
    private final GetPaymentScheduleUseCase       scheduleUseCase;
    private final CalculateWithholdingUseCase     calculateWithholdingUseCase;
    private final FinanceResponseMapper           mapper;

    @PostMapping("/deposits")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<PaymentInstallmentResponse> scheduleDeposit(
            @RequestBody ScheduleDepositRequest request,
            @AuthenticationPrincipal String promoterId) {

        var installment = scheduleDepositUseCase.execute(new ScheduleDepositCommand(
                promoterId, request.linkedContractId(), request.amount(), request.currency(),
                request.dueDate(), request.description()));

        return ResponseEntity
                .created(URI.create("/api/v1/finance/installments/" + installment.getId()))
                .body(mapper.toResponse(installment));
    }

    @PostMapping("/{installmentId}/pay")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<PaymentInstallmentResponse> pay(
            @PathVariable String installmentId,
            @RequestBody RecordInstallmentPaymentRequest request,
            @AuthenticationPrincipal String promoterId) {

        var installment = recordPaymentUseCase.execute(new RecordInstallmentPaymentCommand(
                installmentId, promoterId, request.paidDate(), request.paymentMethod(),
                request.withholdingKind(), request.withholdingRatePercentage(), request.exemptionReason()));

        return ResponseEntity.ok(mapper.toResponse(installment));
    }

    @PostMapping("/{installmentId}/cancel")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<Void> cancel(
            @PathVariable String installmentId,
            @AuthenticationPrincipal String promoterId) {

        cancelUseCase.execute(new CancelInstallmentCommand(installmentId, promoterId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/contracts/{contractId}")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<List<PaymentInstallmentResponse>> schedule(
            @PathVariable String contractId,
            @AuthenticationPrincipal String promoterId) {

        var installments = scheduleUseCase.execute(new GetPaymentScheduleQuery(contractId, promoterId));
        return ResponseEntity.ok(installments.stream().map(mapper::toResponse).toList());
    }

    @PostMapping("/withholding/calculate")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<WithholdingApplicationResponse> calculateWithholding(
            @RequestBody CalculateWithholdingRequest request) {

        var withholding = calculateWithholdingUseCase.execute(new CalculateWithholdingCommand(
                request.taxBaseAmount(), request.currency(), request.withholdingKind(),
                request.ratePercentageOverride(), request.exemptionReason()));

        return ResponseEntity.ok(mapper.toResponse(withholding));
    }
}
