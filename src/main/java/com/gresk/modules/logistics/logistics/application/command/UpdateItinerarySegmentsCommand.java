package com.gresk.modules.logistics.application.command;

import java.util.List;

public record UpdateItinerarySegmentsCommand(String itineraryId, String promoterId,
                                              List<ItinerarySegmentInput> segments) {
}
