package com.gresk.modules.agenda.domain.exception;

public class CalendarAccountAlreadyConnectedException extends RuntimeException {
    public CalendarAccountAlreadyConnectedException(String promoterId, String provider) {
        super("Promoter " + promoterId + " already has a connected " + provider + " calendar account");
    }
}
