package com.gresk.modules.rider.domain.model.valueobject;

public record TransportRequirements(
        String vehicleType,
        Integer passengerCapacity,
        String notes
) {}
