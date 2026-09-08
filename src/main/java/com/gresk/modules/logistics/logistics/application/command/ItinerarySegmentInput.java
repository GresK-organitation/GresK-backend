package com.gresk.modules.logistics.application.command;

import java.time.Instant;
import java.util.List;

public record ItinerarySegmentInput(String id, String type, Instant departureAt, String departureLocation,
                                     Instant arrivalAt, String arrivalLocation, String carrierOrOperator,
                                     String segmentCode, String confirmationReference, String seatOrCapacityInfo,
                                     String voucherUrl, String notes, List<String> travelerIds) {
}
