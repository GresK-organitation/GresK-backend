package com.gresk.modules.quotation.application.command;

public record GenerateEventQuoteCommand(
        String eventId,
        String promoterId,
        String currency
) {}
