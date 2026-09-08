package com.gresk.modules.logistics.application.command;

public record CreateCrewMemberCommand(String promoterId, String name, String defaultRole, String contactPhone,
                                       String contactEmail) {
}
