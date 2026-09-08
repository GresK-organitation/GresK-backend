package com.gresk.modules.show.infrastructure.persistence.adapter;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.show.domain.model.Show;
import com.gresk.modules.show.domain.model.ShowId;
import com.gresk.modules.show.domain.model.ShowStatus;
import com.gresk.modules.show.domain.port.out.ShowRepositoryPort;
import com.gresk.modules.show.infrastructure.persistence.mapper.ShowMapper;
import com.gresk.modules.show.infrastructure.persistence.repository.ShowJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaShowRepositoryAdapter implements ShowRepositoryPort {

    private final ShowJpaRepository repo;
    private final ShowMapper        mapper;

    @Override
    @Transactional
    public Show save(Show show) {
        return mapper.toDomain(repo.save(mapper.toEntity(show)));
    }

    @Override
    public Optional<Show> findById(ShowId id) {
        return repo.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<Show> findByPromoter(PromoterId promoterId) {
        return repo.findByPromoterId(promoterId.value()).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Show> findByPromoterAndStatus(PromoterId promoterId, ShowStatus status) {
        return repo.findByPromoterIdAndStatus(promoterId.value(), status.name()).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Show> findExpirableHolds(Instant now) {
        return repo.findExpirableHolds(now).stream().map(mapper::toDomain).toList();
    }
}
