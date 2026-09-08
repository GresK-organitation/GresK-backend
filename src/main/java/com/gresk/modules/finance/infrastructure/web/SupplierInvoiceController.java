package com.gresk.modules.finance.infrastructure.web;

import com.gresk.modules.finance.application.command.DisputeSupplierInvoiceCommand;
import com.gresk.modules.finance.application.command.MarkSupplierInvoiceAsPaidCommand;
import com.gresk.modules.finance.application.command.RegisterSupplierInvoiceCommand;
import com.gresk.modules.finance.application.command.ValidateSupplierInvoiceCommand;
import com.gresk.modules.finance.application.query.GetEventSupplierInvoicesQuery;
import com.gresk.modules.finance.application.usecase.DisputeSupplierInvoiceUseCase;
import com.gresk.modules.finance.application.usecase.GetEventSupplierInvoicesUseCase;
import com.gresk.modules.finance.application.usecase.MarkSupplierInvoiceAsPaidUseCase;
import com.gresk.modules.finance.application.usecase.RegisterSupplierInvoiceUseCase;
import com.gresk.modules.finance.application.usecase.ValidateSupplierInvoiceUseCase;
import com.gresk.modules.finance.infrastructure.web.dto.DisputeSupplierInvoiceRequest;
import com.gresk.modules.finance.infrastructure.web.dto.RegisterSupplierInvoiceRequest;
import com.gresk.modules.finance.infrastructure.web.dto.SupplierInvoiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/finance/supplier-invoices")
@RequiredArgsConstructor
public class SupplierInvoiceController {

    private final RegisterSupplierInvoiceUseCase   registerUseCase;
    private final ValidateSupplierInvoiceUseCase   validateUseCase;
    private final DisputeSupplierInvoiceUseCase    disputeUseCase;
    private final MarkSupplierInvoiceAsPaidUseCase markPaidUseCase;
    private final GetEventSupplierInvoicesUseCase  listUseCase;
    private final FinanceResponseMapper            mapper;

    @PostMapping
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<SupplierInvoiceResponse> register(
            @RequestBody RegisterSupplierInvoiceRequest request,
            @AuthenticationPrincipal String promoterId) {

        var invoice = registerUseCase.execute(new RegisterSupplierInvoiceCommand(
                promoterId, request.linkedEventId(), request.linkedCostLineId(),
                request.supplierName(), request.supplierTaxId(), request.supplierAddress(),
                request.supplierCountry(), request.supplierEmail(), request.supplierInvoiceNumber(),
                request.amount(), request.taxAmount(), request.currency(),
                request.issueDate(), request.dueDate()));

        return ResponseEntity
                .created(URI.create("/api/v1/finance/supplier-invoices/" + invoice.getId()))
                .body(mapper.toResponse(invoice));
    }

    @PostMapping("/{id}/validate")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<SupplierInvoiceResponse> validate(
            @PathVariable String id,
            @AuthenticationPrincipal String promoterId) {

        var invoice = validateUseCase.execute(new ValidateSupplierInvoiceCommand(id, promoterId));
        return ResponseEntity.ok(mapper.toResponse(invoice));
    }

    @PostMapping("/{id}/dispute")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<SupplierInvoiceResponse> dispute(
            @PathVariable String id,
            @RequestBody(required = false) DisputeSupplierInvoiceRequest request,
            @AuthenticationPrincipal String promoterId) {

        String reason = request != null ? request.reason() : null;
        var invoice = disputeUseCase.execute(new DisputeSupplierInvoiceCommand(id, promoterId, reason));
        return ResponseEntity.ok(mapper.toResponse(invoice));
    }

    @PostMapping("/{id}/mark-paid")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<SupplierInvoiceResponse> markPaid(
            @PathVariable String id,
            @AuthenticationPrincipal String promoterId) {

        var invoice = markPaidUseCase.execute(new MarkSupplierInvoiceAsPaidCommand(id, promoterId));
        return ResponseEntity.ok(mapper.toResponse(invoice));
    }

    @GetMapping("/events/{eventId}")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<List<SupplierInvoiceResponse>> listForEvent(
            @PathVariable String eventId,
            @AuthenticationPrincipal String promoterId) {

        var invoices = listUseCase.execute(new GetEventSupplierInvoicesQuery(eventId, promoterId));
        return ResponseEntity.ok(invoices.stream().map(mapper::toResponse).toList());
    }
}
