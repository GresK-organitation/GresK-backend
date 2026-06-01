package com.gresk.modules.agenda.application.query;

import java.time.Instant;
import java.util.Set;

public record AgendaViewQuery(
        String      promoterId,
        Instant     from,
        Instant     to,
        Set<String> types   // TASK | APPOINTMENT | REMINDER | GRESK_EVENT; null/vacío = todos
) {}
