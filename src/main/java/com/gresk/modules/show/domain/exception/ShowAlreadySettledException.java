package com.gresk.modules.show.domain.exception;

public class ShowAlreadySettledException extends RuntimeException {
    public ShowAlreadySettledException(String showId) {
        super("Show is already settled: " + showId);
    }
}
