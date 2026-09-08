package com.gresk.modules.logistics.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record RoomingListResponse(String id, String tourId, String hotelName, String hotelAddress,
                                   LocalDate checkInDate, LocalDate checkOutDate, List<AllotmentView> allotments,
                                   List<AssignmentView> assignments, Instant updatedAt) {

    public record AllotmentView(String roomType, int quantity, BigDecimal costAmount, String currency) {
    }

    public record AssignmentView(String id, String roomType, List<String> occupantIds, String roomNumber) {
    }
}
