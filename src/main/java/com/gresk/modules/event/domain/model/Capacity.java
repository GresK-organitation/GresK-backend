package com.gresk.modules.event.domain.model;

public record Capacity(int total, int available) {

    public Capacity {
        if (total < 1) {
            throw new IllegalArgumentException("Capacity total must be at least 1");
        }
        if (available < 0) {
            throw new IllegalArgumentException("Capacity available must not be negative");
        }
        if (available > total) {
            throw new IllegalArgumentException("Capacity available must not exceed total");
        }
    }

    public static Capacity of(int total) {
        return new Capacity(total, total);
    }

    public Capacity reserve(int seats) {
        if (seats > available)
            throw new IllegalArgumentException("Not enough available seats");
        return new Capacity(total, available - seats);
    }
}
