package com.gresk.modules.logistics.infrastructure.web.dto;

import com.gresk.modules.logistics.application.command.RoomAllotmentInput;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record UpdateRoomingListRequest(@NotBlank String hotelName, String hotelAddress,
                                        @NotNull LocalDate checkInDate, @NotNull LocalDate checkOutDate,
                                        List<RoomAllotmentInput> allotments) {
}
