package com.gresk.modules.account.infrastructure.web.queries;

import com.gresk.shared.domain.AccountStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;
@Builder
public record AccountsAdminDTO(
        UUID id,
        String name,
        String email,
        String city,
        AccountStatus status,
        Instant createdAt
) {}