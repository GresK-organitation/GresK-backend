package com.gresk.modules.logistics.application.command;

import java.math.BigDecimal;

public record RoomAllotmentInput(String roomType, int quantity, BigDecimal costAmount, String currency) {
}
