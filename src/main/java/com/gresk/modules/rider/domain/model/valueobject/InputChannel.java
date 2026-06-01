package com.gresk.modules.rider.domain.model.valueobject;

public record InputChannel(
        Integer channelNumber,
        String instrument,
        String microphone,
        String inserts,
        String notes
) {
    public InputChannel {
        if (channelNumber == null || channelNumber < 1) throw new IllegalArgumentException("Channel number must be >= 1");
        if (instrument == null || instrument.isBlank()) throw new IllegalArgumentException("Instrument cannot be blank");
    }
}
