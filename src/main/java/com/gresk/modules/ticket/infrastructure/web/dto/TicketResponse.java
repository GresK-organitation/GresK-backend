package com.gresk.modules.ticket.infrastructure.web.dto;

public record TicketResponse(
        String id,
        String eventId,
        String status,
        String qrCode,
        String purchasedAt
) {
}
