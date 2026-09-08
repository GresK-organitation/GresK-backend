package com.gresk.modules.logistics.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateTravelPartyRequest(@NotBlank String tourId) {
}
