package com.gresk.modules.logistics.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCrewMemberRequest(@NotBlank String name, @NotBlank String defaultRole, String contactPhone,
                                       String contactEmail) {
}
