package com.gresk.modules.ticket.infrastructure.web;

import com.gresk.modules.ticket.application.usecase.GetTicketQrQuery;
import com.gresk.modules.ticket.application.usecase.GetTicketQrUseCase;
import com.gresk.modules.ticket.application.usecase.GetUserTicketsQuery;
import com.gresk.modules.ticket.application.usecase.GetUserTicketsUseCase;
import com.gresk.modules.ticket.application.usecase.PurchaseTicketCommand;
import com.gresk.modules.ticket.domain.model.Ticket;
import com.gresk.modules.ticket.infrastructure.TransactionalPurchaseTicketService;
import com.gresk.modules.ticket.infrastructure.web.dto.PurchaseTicketRequest;
import com.gresk.modules.ticket.infrastructure.web.dto.TicketResponse;
import com.gresk.modules.user.domain.model.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Tickets", description = "Ticket purchase and retrieval endpoints")
public class TicketController {

    private final TransactionalPurchaseTicketService purchaseTicketService;
    private final GetUserTicketsUseCase getUserTicketsUseCase;
    private final GetTicketQrUseCase getTicketQrUseCase;

    @PostMapping("/api/v1/tickets")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Purchase a ticket for an event")
    @ApiResponse(responseCode = "201", description = "Ticket purchased successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    @ApiResponse(responseCode = "409", description = "User already has a ticket for this event")
    @ApiResponse(responseCode = "422", description = "Event is sold out or not published")
    public ResponseEntity<TicketResponse> purchase(
            @Valid @RequestBody PurchaseTicketRequest request,
            @AuthenticationPrincipal UserId userId) {
        Ticket ticket = purchaseTicketService.execute(
                new PurchaseTicketCommand(userId.value().toString(), request.eventId()));
        return ResponseEntity.status(201).body(toResponse(ticket));
    }

    @GetMapping("/api/v1/users/me/tickets")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List authenticated user's tickets")
    @ApiResponse(responseCode = "200", description = "List of tickets")
    public ResponseEntity<List<TicketResponse>> listMyTickets(
            @AuthenticationPrincipal UserId userId) {
        List<TicketResponse> responses = getUserTicketsUseCase
                .execute(new GetUserTicketsQuery(userId.value().toString()))
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/api/v1/tickets/{id}/qr")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get QR image for a ticket")
    @ApiResponse(responseCode = "200", description = "PNG image of the QR code")
    @ApiResponse(responseCode = "403", description = "User does not own this ticket")
    @ApiResponse(responseCode = "404", description = "Ticket not found")
    public ResponseEntity<byte[]> getQr(
            @PathVariable String id,
            @AuthenticationPrincipal UserId userId) {
        byte[] image = getTicketQrUseCase.execute(
                new GetTicketQrQuery(id, userId.value().toString()));
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(image);
    }

    private TicketResponse toResponse(Ticket ticket) {
        return new TicketResponse(
                ticket.getId().value().toString(),
                ticket.getEventId().value().toString(),
                ticket.getStatus().name(),
                ticket.getQrCode().value(),
                ticket.getPurchasedAt().toString()
        );
    }
}
