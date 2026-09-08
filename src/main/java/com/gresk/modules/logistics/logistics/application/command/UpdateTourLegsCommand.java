package com.gresk.modules.logistics.application.command;

import java.util.List;

public record UpdateTourLegsCommand(String tourId, String promoterId, List<TourLegInput> legs) {
}
