package com.gresk.modules.logistics.domain.exception;

public class CrewMemberNotFoundException extends RuntimeException {
    public CrewMemberNotFoundException(String crewMemberId) {
        super("Crew member not found: " + crewMemberId);
    }
}
