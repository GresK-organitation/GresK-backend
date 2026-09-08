package com.gresk.modules.rider.application.command;

import java.util.Map;

public record AddLineItemCommand(
        String riderId,
        String promoterId,
        String category,
        String description,
        int quantity,
        boolean required,
        Map<String, String> attributes,
        String notes
) {}
