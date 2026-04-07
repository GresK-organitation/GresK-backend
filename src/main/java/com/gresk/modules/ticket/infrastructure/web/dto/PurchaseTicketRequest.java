package com.gresk.modules.ticket.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public record PurchaseTicketRequest(
        @NotBlank(message = "eventId must not be blank") String eventId
) {
}
