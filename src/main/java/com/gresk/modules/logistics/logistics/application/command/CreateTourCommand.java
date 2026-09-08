package com.gresk.modules.logistics.application.command;

import java.time.LocalDate;
import java.util.List;

public record CreateTourCommand(String promoterId, String artistId, String name, LocalDate startDate,
                                 LocalDate endDate, String notes, List<TourLegInput> legs) {
}
