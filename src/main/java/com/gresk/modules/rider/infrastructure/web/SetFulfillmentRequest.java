package com.gresk.modules.rider.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

public record SetFulfillmentRequest(
        @NotBlank String fulfillmentSource
) {}
