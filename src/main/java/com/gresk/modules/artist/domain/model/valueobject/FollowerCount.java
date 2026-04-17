package com.gresk.modules.artist.domain.model.valueobject;

import com.gresk.modules.artist.domain.exception.InvalidFollowerCountException;

public record FollowerCount(String value) {

    private static final int MAX_LENGTH = 50;

    public FollowerCount {
        if (value == null) {
            value = "";
        } else {
            value = value.trim();
            if (value.length() > MAX_LENGTH) {
                throw new InvalidFollowerCountException(
                        String.format("Follower count cannot exceed %d characters", MAX_LENGTH));
            }
        }
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }

    public static FollowerCount of(String value) {
        return new FollowerCount(value);
    }

    public static FollowerCount empty() {
        return new FollowerCount("");
    }

    public static FollowerCount reconstitute(String value) {
        return new FollowerCount(value != null ? value : "");
    }

    @Override
    public String toString() {
        return value;
    }
}
