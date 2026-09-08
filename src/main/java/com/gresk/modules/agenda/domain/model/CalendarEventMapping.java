package com.gresk.modules.agenda.domain.model;

import java.time.Instant;
import java.util.UUID;

/** Bookkeeping de sincronización incremental (etag/updated inspirado en Google Calendar API). */
public record CalendarEventMapping(UUID localEntryId, LocalEntryType localType, String externalEventId,
                                    String externalEtag, Instant lastPushedAt, Instant lastPulledAt) {

    public CalendarEventMapping {
        if (localEntryId == null) {
            throw new IllegalArgumentException("CalendarEventMapping localEntryId must not be null");
        }
        if (localType == null) {
            throw new IllegalArgumentException("CalendarEventMapping localType must not be null");
        }
        if (externalEventId == null || externalEventId.isBlank()) {
            throw new IllegalArgumentException("CalendarEventMapping externalEventId must not be blank");
        }
    }
}
