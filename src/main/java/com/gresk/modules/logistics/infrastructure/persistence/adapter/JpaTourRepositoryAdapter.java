package com.gresk.modules.logistics.infrastructure.persistence.adapter;

import com.gresk.modules.logistics.domain.model.Tour;
import com.gresk.modules.logistics.domain.model.TourId;
import com.gresk.modules.logistics.domain.model.TourStatus;
import com.gresk.modules.logistics.domain.port.out.TourRepositoryPort;
import com.gresk.modules.logistics.infrastructure.persistence.mapper.TourMapper;
import com.gresk.modules.logistics.infrastructure.persistence.repository.TourJpaRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaTourRepositoryAdapter implements TourRepositoryPort {

    private final TourJpaRepository jpaRepository;
    private final TourMapper mapper;

    @Override
    @Transactional
    public Tour save(Tour tour) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(tour)));
    }

    @Override
    public Optional<Tour> findById(TourId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<Tour> findByIdAndPromoterId(TourId id, PromoterId promoterId) {
        return jpaRepository.findByIdAndPromoterId(id.value(), promoterId.value()).map(mapper::toDomain);
    }

    @Override
    public List<Tour> findByPromoterId(PromoterId promoterId, TourStatus status) {
        var entities = status == null
                ? jpaRepository.findByPromoterId(promoterId.value())
                : jpaRepository.findByPromoterIdAndStatus(promoterId.value(), status.name());
        return entities.stream().map(mapper::toDomain).toList();
    }
}
