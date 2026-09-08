package com.gresk.modules.artist.domain.exception;

public class RosterMemberNotFoundException extends RuntimeException {
    public RosterMemberNotFoundException(String rosterMemberId) {
        super("Roster member not found: " + rosterMemberId);
    }
}
