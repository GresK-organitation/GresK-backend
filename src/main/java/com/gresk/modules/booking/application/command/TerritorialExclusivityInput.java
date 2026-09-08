package com.gresk.modules.booking.application.command;

public record TerritorialExclusivityInput(String country, String region, String city, Double radiusKm,
                                           int daysBefore, int daysAfter) {
}
