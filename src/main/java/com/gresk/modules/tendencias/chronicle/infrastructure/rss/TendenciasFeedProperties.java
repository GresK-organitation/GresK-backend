package com.gresk.modules.tendencias.chronicle.infrastructure.rss;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "gresk.tendencias.chronicle")
public record TendenciasFeedProperties(
        @DefaultValue("1800000") long pollingIntervalMs,
        @DefaultValue("30") int maxEntriesPerFetch
) {
}
