package com.gresk.modules.quotation.infrastructure.web;

import com.gresk.modules.quotation.application.command.GenerateEventQuoteCommand;
import com.gresk.modules.quotation.application.usecase.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/events/{eventId}/quote")
@RequiredArgsConstructor
public class QuotationController {

    private final GenerateEventQuoteUseCase       generateUseCase;
    private final GetEventQuoteUseCase            getUseCase;
    private final AssignSupplierToQuoteLineUseCase assignSupplierUseCase;
    private final ConfirmEventQuoteUseCase        confirmUseCase;
    private final EventQuoteResponseMapper        mapper;

    // ── POST /api/v1/events/{eventId}/quote ──────────────────────────────────
    @PostMapping
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<EventQuoteResponse> generate(
            @PathVariable String eventId,
            @RequestBody @Valid GenerateEventQuoteRequest request,
            @AuthenticationPrincipal String promoterId) {

        var quote = generateUseCase.execute(new GenerateEventQuoteCommand(eventId, promoterId, request.currency()));
        return ResponseEntity
                .created(URI.create("/api/v1/events/" + eventId + "/quote"))
                .body(mapper.toResponse(quote));
    }

    // ── GET /api/v1/events/{eventId}/quote ───────────────────────────────────
    @GetMapping
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<EventQuoteResponse> get(@PathVariable String eventId) {
        return ResponseEntity.ok(mapper.toResponse(getUseCase.execute(eventId)));
    }

    // ── POST /api/v1/events/{eventId}/quote/lines/{lineId}/supplier ─────────
    @PostMapping("/lines/{lineId}/supplier")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<EventQuoteResponse> assignSupplier(
            @PathVariable String eventId,
            @PathVariable String lineId,
            @RequestBody @Valid AssignSupplierRequest request,
            @AuthenticationPrincipal String promoterId) {

        String quoteId = getUseCase.execute(eventId).getId().toString();
        assignSupplierUseCase.execute(quoteId, promoterId, lineId, request.supplierId(), request.catalogItemId());
        return ResponseEntity.ok(mapper.toResponse(getUseCase.execute(eventId)));
    }

    // ── POST /api/v1/events/{eventId}/quote/confirm ──────────────────────────
    @PostMapping("/confirm")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<EventQuoteResponse> confirm(
            @PathVariable String eventId,
            @AuthenticationPrincipal String promoterId) {

        String quoteId = getUseCase.execute(eventId).getId().toString();
        var quote = confirmUseCase.execute(quoteId, promoterId);
        return ResponseEntity.ok(mapper.toResponse(quote));
    }
}
