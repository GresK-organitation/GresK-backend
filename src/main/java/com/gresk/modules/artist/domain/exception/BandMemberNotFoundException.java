package com.gresk.modules.artist.domain.exception;

public class BandMemberNotFoundException extends RuntimeException {
    public BandMemberNotFoundException(String bandMemberId) {
        super("Band member not found: " + bandMemberId);
    }
}
