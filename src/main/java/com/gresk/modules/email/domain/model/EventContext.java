package com.gresk.modules.email.domain.model;

import java.util.Set;
import java.util.UUID;

/**
 * Contexto de eventos del promotor que el clasificador de reglas usa como
 * señal determinista: hilos de correo ya vinculados a un evento conocido.
 */
public record EventContext(UUID linkedEventId, Set<String> activeThreadIds) {

    public EventContext {
        activeThreadIds = activeThreadIds != null ? Set.copyOf(activeThreadIds) : Set.of();
    }

    public static EventContext empty() {
        return new EventContext(null, Set.of());
    }

    public static EventContext forThread(UUID linkedEventId, String threadIdExternal) {
        return new EventContext(linkedEventId,
                threadIdExternal != null ? Set.of(threadIdExternal) : Set.of());
    }

    public boolean hasActiveThread(String threadIdExternal) {
        return threadIdExternal != null && activeThreadIds.contains(threadIdExternal);
    }
}
