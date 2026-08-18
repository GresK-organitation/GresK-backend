package com.gresk.modules.tendencias.chronicle.infrastructure.persistence.adapter;

import com.gresk.modules.tendencias.chronicle.domain.model.Chronicle;
import com.gresk.modules.tendencias.chronicle.domain.model.ChronicleId;
import com.gresk.modules.tendencias.chronicle.domain.model.ChronicleStatus;
import com.gresk.modules.tendencias.chronicle.domain.model.FeedSourceId;
import com.gresk.modules.tendencias.chronicle.domain.port.out.ChronicleRepositoryPort;
import com.gresk.modules.tendencias.chronicle.infrastructure.persistence.mapper.ChronicleMapper;
import com.gresk.modules.tendencias.chronicle.infrastructure.persistence.repository.ChronicleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaChronicleRepositoryAdapter implements ChronicleRepositoryPort {

    private final ChronicleJpaRepository repository;
    private final ChronicleMapper mapper;

    @Override
    public void save(Chronicle chronicle) {
        repository.save(mapper.toEntity(chronicle));
    }

    @Override
    public boolean existsByFeedSourceIdAndGuid(FeedSourceId feedSourceId, String guid) {
        return repository.existsByFeedSourceIdAndGuid(feedSourceId.value(), guid);
    }

    @Override
    public Optional<Chronicle> findById(ChronicleId id) {
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<Chronicle> findPublished(int page, int size) {
        return repository.findByStatusOrderByOriginalPublishedAtDesc(ChronicleStatus.PUBLISHED, PageRequest.of(page, size))
                .map(mapper::toDomain)
                .getContent();
    }
}
