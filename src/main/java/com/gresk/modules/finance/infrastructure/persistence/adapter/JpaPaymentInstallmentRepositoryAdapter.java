package com.gresk.modules.finance.infrastructure.persistence.adapter;

import com.gresk.modules.finance.domain.model.InstallmentStatus;
import com.gresk.modules.finance.domain.model.PaymentInstallment;
import com.gresk.modules.finance.domain.model.PaymentInstallmentId;
import com.gresk.modules.finance.domain.port.out.PaymentInstallmentRepositoryPort;
import com.gresk.modules.finance.infrastructure.persistence.mapper.PaymentInstallmentMapper;
import com.gresk.modules.finance.infrastructure.persistence.repository.PaymentInstallmentJpaRepository;
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
public class JpaPaymentInstallmentRepositoryAdapter implements PaymentInstallmentRepositoryPort {

    private final PaymentInstallmentJpaRepository repo;
    private final PaymentInstallmentMapper         mapper;

    @Override
    @Transactional
    public PaymentInstallment save(PaymentInstallment installment) {
        return mapper.toDomain(repo.save(mapper.toEntity(installment)));
    }

    @Override
    public Optional<PaymentInstallment> findById(PaymentInstallmentId id) {
        return repo.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<PaymentInstallment> findByLinkedContractId(UUID linkedContractId) {
        return repo.findByLinkedContractId(linkedContractId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<PaymentInstallment> findByLinkedSettlementId(UUID linkedSettlementId) {
        return repo.findByLinkedSettlementId(linkedSettlementId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<PaymentInstallment> findByPromoterIdAndStatus(PromoterId promoterId, InstallmentStatus status) {
        return repo.findByPromoterIdAndStatus(promoterId.value(), status.name()).stream().map(mapper::toDomain).toList();
    }
}
