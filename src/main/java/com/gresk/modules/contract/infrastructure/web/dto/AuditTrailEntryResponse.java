package com.gresk.modules.contract.infrastructure.web.dto;

import com.gresk.modules.contract.domain.model.valueobject.AuditAction;

import java.time.Instant;
import java.util.Map;

public record AuditTrailEntryResponse(
        String       id,
        AuditAction  action,
        String       actor,
        Instant      occurredAt,
        String       ipAddress,
        Map<String, Object> metadata,
        String       documentHash
) {}
