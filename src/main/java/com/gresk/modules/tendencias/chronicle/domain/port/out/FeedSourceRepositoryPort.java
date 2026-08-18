package com.gresk.modules.tendencias.chronicle.domain.port.out;

import com.gresk.modules.tendencias.chronicle.domain.model.FeedSource;
import com.gresk.modules.tendencias.chronicle.domain.model.FeedSourceId;

import java.util.List;
import java.util.Optional;

public interface FeedSourceRepositoryPort {

    void save(FeedSource feedSource);

    Optional<FeedSource> findById(FeedSourceId id);

    boolean existsByFeedUrl(String feedUrl);

    List<FeedSource> findAll();

    List<FeedSource> findAllApproved();
}
