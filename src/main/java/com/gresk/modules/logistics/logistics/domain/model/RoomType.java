package com.gresk.modules.logistics.domain.model;

public enum RoomType {
    SINGLE(1),
    DOUBLE(2),
    TWIN(2);

    private final int maxOccupancy;

    RoomType(int maxOccupancy) {
        this.maxOccupancy = maxOccupancy;
    }

    public int maxOccupancy() {
        return maxOccupancy;
    }
}
