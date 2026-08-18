package com.gresk.modules.tendencias.stats.infrastructure.persistence;

import java.util.UUID;

public interface HighestSellThroughRow {
    UUID getEventId();
    String getEventTitle();
    Double getSellThroughPercent();
}
