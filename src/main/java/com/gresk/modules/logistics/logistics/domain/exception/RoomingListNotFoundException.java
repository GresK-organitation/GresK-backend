package com.gresk.modules.logistics.domain.exception;

public class RoomingListNotFoundException extends RuntimeException {
    public RoomingListNotFoundException(String roomingListId) {
        super("Rooming list not found: " + roomingListId);
    }
}
