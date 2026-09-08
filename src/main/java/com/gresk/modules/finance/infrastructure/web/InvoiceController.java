package com.gresk.modules.finance.infrastructure.web;

import com.gresk.modules.finance.application.command.CancelInvoiceCommand;
import com.gresk.modules.finance.application.command.GenerateInvoicePdfCommand;
import com.gresk.modules.finance.application.command.IssueInvoiceCommand;
import com.gresk.modules.finance.application.command.MarkInvoicePaidCommand;
import com.gresk.modules.finance.application.query.GetEventInvoicesQuery;
import com.gresk.modules.finance.application.usecase.CancelInvoiceUseCase;
import com.gresk.modules.finance.application.usecase.GenerateInvoicePdfUseCase;
import com.gresk.modules.finance.application.usecase.GetEventInvoicesUseCase;
import com.gresk.modules.finance.application.usecase.IssueInvoiceUseCase;
import com.gresk.modules.finance.application.usecase.MarkInvoicePaidUseCase;
import com.gresk.modules.finance.infrastructure.web.dto.InvoiceResponse;
import com.gresk.modules.finance.infrastructure.web.dto.IssueInvoiceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/finance/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final IssueInvoiceUseCase       issueUseCase;
    private final CancelInvoiceUseCase      cancelUseCase;
    private final MarkInvoicePaidUseCase    markPaidUseCase;
    private final GenerateInvoicePdfUseCase pdfUseCase;
    private final GetEventInvoicesUseCase   listUseCase;
    private final FinanceResponseMapper     mapper;

    @PostMapping
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<InvoiceResponse> issue(
            @RequestBody IssueInvoiceRequest request,
            @AuthenticationPrincipal String promoterId) {

        var invoice = issueUseCase.execute(new IssueInvoiceCommand(
                promoterId, request.linkedEventId(), request.recipientName(), request.recipientTaxId(),
                request.recipientAddress(), request.recipientCountry(), request.recipientEmail(),
                request.currency(), request.lines(), request.issueDate(), request.dueDate()));

        return ResponseEntity
                .created(URI.create("/api/v1/finance/invoices/" + invoice.getId()))
                .body(mapper.toResponse(invoice));
    }

    @PostMapping("/{invoiceId}/cancel")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<Void> cancel(
            @PathVariable String invoiceId,
            @AuthenticationPrincipal String promoterId) {

        cancelUseCase.execute(new CancelInvoiceCommand(invoiceId, promoterId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{invoiceId}/mark-paid")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<InvoiceResponse> markPaid(
            @PathVariable String invoiceId,
            @AuthenticationPrincipal String promoterId) {

        var invoice = markPaidUseCase.execute(new MarkInvoicePaidCommand(invoiceId, promoterId));
        return ResponseEntity.ok(mapper.toResponse(invoice));
    }

    @PostMapping("/{invoiceId}/pdf")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<byte[]> pdf(
            @PathVariable String invoiceId,
            @AuthenticationPrincipal String promoterId) {

        byte[] pdf = pdfUseCase.execute(new GenerateInvoicePdfCommand(invoiceId, promoterId));
        String filename = "invoice-" + invoiceId + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/events/{eventId}")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<List<InvoiceResponse>> listForEvent(
            @PathVariable String eventId,
            @AuthenticationPrincipal String promoterId) {

        var invoices = listUseCase.execute(new GetEventInvoicesQuery(eventId, promoterId));
        return ResponseEntity.ok(invoices.stream().map(mapper::toResponse).toList());
    }
}
