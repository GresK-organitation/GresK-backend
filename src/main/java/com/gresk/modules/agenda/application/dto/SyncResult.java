package com.gresk.modules.agenda.application.dto;

import java.time.Instant;

public record SyncResult(int pushedCount, int pulledCount, int conflictCount, Instant syncedAt) {
}
