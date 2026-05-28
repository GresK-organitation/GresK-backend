package com.gresk.modules.event.application.query;

import java.time.Instant;

public record CalendarEventsQuery(String promoterId, Instant from, Instant to) {}
