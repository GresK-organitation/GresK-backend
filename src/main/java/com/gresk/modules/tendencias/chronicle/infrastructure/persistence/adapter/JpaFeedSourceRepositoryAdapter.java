package com.gresk.modules.tendencias.chronicle.infrastructure.persistence.adapter;

import com.gresk.modules.tendencias.chronicle.domain.model.FeedSource;
import com.gresk.modules.tendencias.chronicle.domain.model.FeedSourceId;
import com.gresk.modules.tendencias.chronicle.domain.model.FeedSourceStatus;
import com.gresk.modules.tendencias.chronicle.domain.port.out.FeedSourceRepositoryPort;
import com.gresk.modules.tendencias.chronicle.infrastructure.persistence.mapper.FeedSourceMapper;
import com.gresk.modules.tendencias.chronicle.infrastructure.persistence.repository.FeedSourceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaFeedSourceRepositoryAdapter implements FeedSourceRepositoryPort {

    private final FeedSourceJpaRepository repository;
    private final FeedSourceMapper mapper;

    @Override
    public void save(FeedSource feedSource) {
        repository.save(mapper.toEntity(feedSource));
    }

    @Override
    public Optional<FeedSource> findById(FeedSourceId id) {
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public boolean existsByFeedUrl(String feedUrl) {
        return repository.existsByFeedUrl(feedUrl);
    }

    @Override
    public List<FeedSource> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<FeedSource> findAllApproved() {
        return repository.findByStatus(FeedSourceStatus.APPROVED).stream().map(mapper::toDomain).toList();
    }
}
