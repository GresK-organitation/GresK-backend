package com.gresk.modules.finance.infrastructure.persistence.adapter;

import com.gresk.modules.finance.domain.model.SettlementAgreement;
import com.gresk.modules.finance.domain.model.SettlementAgreementId;
import com.gresk.modules.finance.domain.model.SettlementAgreementStatus;
import com.gresk.modules.finance.domain.port.out.SettlementAgreementRepositoryPort;
import com.gresk.modules.finance.infrastructure.persistence.mapper.SettlementAgreementMapper;
import com.gresk.modules.finance.infrastructure.persistence.repository.SettlementAgreementJpaRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaSettlementAgreementRepositoryAdapter implements SettlementAgreementRepositoryPort {

    private final SettlementAgreementJpaRepository repo;
    private final SettlementAgreementMapper         mapper;

    @Override
    @Transactional
    public SettlementAgreement save(SettlementAgreement agreement) {
        return mapper.toDomain(repo.save(mapper.toEntity(agreement)));
    }

    @Override
    public Optional<SettlementAgreement> findById(SettlementAgreementId id) {
        return repo.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<SettlementAgreement> findActiveByLinkedContractId(UUID linkedContractId) {
        return repo.findByLinkedContractIdAndStatus(linkedContractId, SettlementAgreementStatus.ACTIVE.name())
                .map(mapper::toDomain);
    }

    @Override
    public List<SettlementAgreement> findByPromoterId(PromoterId promoterId) {
        return repo.findByPromoterId(promoterId.value()).stream().map(mapper::toDomain).toList();
    }
}
