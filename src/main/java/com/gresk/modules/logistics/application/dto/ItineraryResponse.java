package com.gresk.modules.logistics.application.dto;

import java.time.Instant;
import java.util.List;

public record ItineraryResponse(String id, String tourId, List<SegmentView> segments, Instant updatedAt) {

    public record SegmentView(String id, String type, Instant departureAt, String departureLocation,
                               Instant arrivalAt, String arrivalLocation, String carrierOrOperator,
                               String segmentCode, String confirmationReference, String seatOrCapacityInfo,
                               String voucherUrl, String notes, List<String> travelerIds) {
    }
}
