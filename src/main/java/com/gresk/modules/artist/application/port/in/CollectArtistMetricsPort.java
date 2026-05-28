package com.gresk.modules.artist.application.port.in;

public interface CollectArtistMetricsPort {

    void collectMetrics();

    void purgeOlderThan(int days);
}
