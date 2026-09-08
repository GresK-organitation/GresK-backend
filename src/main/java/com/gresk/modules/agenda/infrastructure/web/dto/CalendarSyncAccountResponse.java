package com.gresk.modules.agenda.infrastructure.web.dto;

import java.time.Instant;

public record CalendarSyncAccountResponse(String id, String provider, String externalAccountEmail,
                                           String status, Instant lastSyncedAt, String lastError,
                                           Instant createdAt) {
}
