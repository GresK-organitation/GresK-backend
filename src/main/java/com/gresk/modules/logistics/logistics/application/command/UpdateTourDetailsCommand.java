package com.gresk.modules.logistics.application.command;

import java.time.LocalDate;

public record UpdateTourDetailsCommand(String tourId, String promoterId, String name, LocalDate startDate,
                                        LocalDate endDate, String notes) {
}
