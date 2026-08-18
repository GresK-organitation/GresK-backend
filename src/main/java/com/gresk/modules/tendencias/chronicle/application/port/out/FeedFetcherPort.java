package com.gresk.modules.tendencias.chronicle.application.port.out;

import com.gresk.modules.tendencias.chronicle.domain.model.FeedSource;

import java.util.List;

public interface FeedFetcherPort {
    List<FetchedFeedEntry> fetch(FeedSource source);
}
