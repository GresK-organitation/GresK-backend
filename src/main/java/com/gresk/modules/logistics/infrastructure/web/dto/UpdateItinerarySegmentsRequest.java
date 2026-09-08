package com.gresk.modules.logistics.infrastructure.web.dto;

import com.gresk.modules.logistics.application.command.ItinerarySegmentInput;

import java.util.List;

public record UpdateItinerarySegmentsRequest(List<ItinerarySegmentInput> segments) {
}
