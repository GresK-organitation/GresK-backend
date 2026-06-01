package com.gresk.modules.rider.infrastructure.persistence.adapter;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.rider.domain.model.AlertId;
import com.gresk.modules.rider.domain.model.RiderAlert;
import com.gresk.modules.rider.domain.port.out.RiderAlertRepositoryPort;
import com.gresk.modules.rider.infrastructure.persistence.mapper.RiderAlertMapper;
import com.gresk.modules.rider.infrastructure.persistence.repository.RiderAlertJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaRiderAlertRepositoryAdapter implements RiderAlertRepositoryPort {

    private final RiderAlertJpaRepository repo;
    private final RiderAlertMapper        mapper;

    @Override
    @Transactional
    public RiderAlert save(RiderAlert alert) {
        return mapper.toDomain(repo.save(mapper.toEntity(alert)));
    }

    @Override
    public Optional<RiderAlert> findById(AlertId id) {
        return repo.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<RiderAlert> findUnreadByPromoterId(PromoterId promoterId) {
        return repo.findByPromoterIdAndReadFalseOrderByCreatedAtDesc(promoterId.value()).stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    public long countUnreadByPromoterId(PromoterId promoterId) {
        return repo.countByPromoterIdAndReadFalse(promoterId.value());
    }
}
