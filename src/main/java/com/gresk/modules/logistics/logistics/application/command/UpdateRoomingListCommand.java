package com.gresk.modules.logistics.application.command;

import java.time.LocalDate;
import java.util.List;

public record UpdateRoomingListCommand(String roomingListId, String promoterId, String hotelName,
                                        String hotelAddress, LocalDate checkInDate, LocalDate checkOutDate,
                                        List<RoomAllotmentInput> allotments) {
}
