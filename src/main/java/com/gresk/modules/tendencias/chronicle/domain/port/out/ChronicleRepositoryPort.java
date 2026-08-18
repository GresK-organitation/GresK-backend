package com.gresk.modules.tendencias.chronicle.domain.port.out;

import com.gresk.modules.tendencias.chronicle.domain.model.Chronicle;
import com.gresk.modules.tendencias.chronicle.domain.model.ChronicleId;
import com.gresk.modules.tendencias.chronicle.domain.model.FeedSourceId;

import java.util.List;
import java.util.Optional;

public interface ChronicleRepositoryPort {

    void save(Chronicle chronicle);

    boolean existsByFeedSourceIdAndGuid(FeedSourceId feedSourceId, String guid);

    Optional<Chronicle> findById(ChronicleId id);

    List<Chronicle> findPublished(int page, int size);
}
