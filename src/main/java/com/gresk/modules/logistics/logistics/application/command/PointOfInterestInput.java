package com.gresk.modules.logistics.application.command;

public record PointOfInterestInput(String name, String category, String address, Double latitude, Double longitude,
                                    String notes) {
}
