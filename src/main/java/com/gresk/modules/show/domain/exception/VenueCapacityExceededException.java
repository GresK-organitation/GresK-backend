package com.gresk.modules.show.domain.exception;

public class VenueCapacityExceededException extends RuntimeException {
    public VenueCapacityExceededException(int requested, int maxCapacity) {
        super("Requested capacity " + requested + " exceeds venue configuration maximum of " + maxCapacity);
    }
}
