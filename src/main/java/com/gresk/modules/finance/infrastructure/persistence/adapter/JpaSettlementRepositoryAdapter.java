package com.gresk.modules.finance.infrastructure.persistence.adapter;

import com.gresk.modules.finance.domain.model.Settlement;
import com.gresk.modules.finance.domain.model.SettlementAgreementId;
import com.gresk.modules.finance.domain.model.SettlementId;
import com.gresk.modules.finance.domain.port.out.SettlementRepositoryPort;
import com.gresk.modules.finance.infrastructure.persistence.mapper.SettlementMapper;
import com.gresk.modules.finance.infrastructure.persistence.repository.SettlementJpaRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaSettlementRepositoryAdapter implements SettlementRepositoryPort {

    private final SettlementJpaRepository repo;
    private final SettlementMapper         mapper;

    @Override
    @Transactional
    public Settlement save(Settlement settlement) {
        return mapper.toDomain(repo.save(mapper.toEntity(settlement)));
    }

    @Override
    public Optional<Settlement> findById(SettlementId id) {
        return repo.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<Settlement> findBySettlementAgreementId(SettlementAgreementId agreementId) {
        return repo.findBySettlementAgreementId(agreementId.value()).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Settlement> findByPromoterId(PromoterId promoterId) {
        return repo.findByPromoterId(promoterId.value()).stream().map(mapper::toDomain).toList();
    }
}
