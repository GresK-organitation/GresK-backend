package com.gresk.modules.ticket.domain.model;

public record QrCode(String value) {

    public QrCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("QrCode value must not be null or blank");
        }
    }

    public static QrCode of(String value) {
        return new QrCode(value);
    }
}
