package com.gresk.modules.artist.application.port.in;

/** Invocado por el scheduler; itera todos los artistas con nombre vinculable a Bandsintown. */
public interface CollectBandsintownTractionPort {
    void execute();
    void purgeOlderThan(int retentionDays);
}
