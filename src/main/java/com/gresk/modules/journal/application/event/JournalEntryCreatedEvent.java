package com.gresk.modules.journal.application.event;

import java.util.UUID;

/** Se publica tras persistir una entrada de diario; dispara el recálculo async del ADN Musical. */
public record JournalEntryCreatedEvent(UUID userId) {}
