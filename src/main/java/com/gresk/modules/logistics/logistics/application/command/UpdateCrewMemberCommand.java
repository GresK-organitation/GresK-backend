package com.gresk.modules.logistics.application.command;

import java.util.List;

public record UpdateCrewMemberCommand(String crewMemberId, String promoterId, String name, String defaultRole,
                                       String contactPhone, String contactEmail,
                                       List<IdentityDocumentInput> documents) {
}
