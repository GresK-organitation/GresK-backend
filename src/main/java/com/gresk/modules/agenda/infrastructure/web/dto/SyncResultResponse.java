package com.gresk.modules.agenda.infrastructure.web.dto;

import java.time.Instant;

public record SyncResultResponse(int pushedCount, int pulledCount, int conflictCount, Instant syncedAt) {
}
