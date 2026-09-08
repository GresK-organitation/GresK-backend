package com.gresk.modules.booking.infrastructure.web.dto;

import com.gresk.modules.booking.application.command.VenueRefInput;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record TerritorialExclusivityCheckRequest(@NotBlank String artistId, @NotNull VenueRefInput venue,
                                                   @NotNull Instant eventDate, int daysBefore, int daysAfter,
                                                   String excludeBookingId) {
}
