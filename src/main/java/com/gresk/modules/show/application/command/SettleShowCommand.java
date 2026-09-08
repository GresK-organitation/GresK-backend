package com.gresk.modules.show.application.command;

import java.math.BigDecimal;

public record SettleShowCommand(String showId, String promoterId, int actualAttendance,
                                 BigDecimal actualRevenue, BigDecimal actualCosts) {
}
