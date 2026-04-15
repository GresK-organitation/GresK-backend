package com.gresk.modules.account.application.dto;

import com.gresk.shared.domain.AccountStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;
@Builder
public record AccountAdminSummary(
        UUID id,
        String name,
        String email,
        String city,
        AccountStatus status,
        Instant createdAt
) {}