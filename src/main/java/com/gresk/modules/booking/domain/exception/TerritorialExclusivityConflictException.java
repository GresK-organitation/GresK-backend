package com.gresk.modules.booking.domain.exception;

import java.util.List;
import java.util.UUID;

public class TerritorialExclusivityConflictException extends RuntimeException {

    private final List<UUID> conflictingBookingIds;

    public TerritorialExclusivityConflictException(List<UUID> conflictingBookingIds) {
        super("Territorial exclusivity conflict with " + conflictingBookingIds.size() + " existing booking(s)");
        this.conflictingBookingIds = conflictingBookingIds;
    }

    public List<UUID> getConflictingBookingIds() {
        return conflictingBookingIds;
    }
}
