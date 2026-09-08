package com.gresk.modules.logistics.infrastructure.web.dto;

import com.gresk.modules.logistics.application.command.IdentityDocumentInput;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UpdateCrewMemberRequest(@NotBlank String name, @NotBlank String defaultRole, String contactPhone,
                                       String contactEmail, List<IdentityDocumentInput> documents) {
}
