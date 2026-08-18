package com.gresk.modules.tendencias.stats.infrastructure.persistence;

public interface MostVisitedVenueRow {
    String getVenueName();
    Long getAttendeeCount();
    Long getEventCount();
}
