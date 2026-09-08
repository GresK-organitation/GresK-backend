package com.gresk.modules.contract.infrastructure.persistence.adapter;

import com.gresk.modules.contract.domain.model.Contract;
import com.gresk.modules.contract.domain.model.ContractId;
import com.gresk.modules.contract.domain.model.ContractStatus;
import com.gresk.modules.contract.domain.model.ContractType;
import com.gresk.modules.contract.domain.port.out.ContractRepositoryPort;
import com.gresk.modules.contract.domain.port.out.ContractStats;
import com.gresk.modules.contract.infrastructure.persistence.mapper.ContractMapper;
import com.gresk.modules.contract.infrastructure.persistence.repository.ContractJpaRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Year;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaContractRepositoryAdapter implements ContractRepositoryPort {

    private final ContractJpaRepository repo;
    private final ContractMapper        mapper;

    @Override
    @Transactional
    public Contract save(Contract contract) {
        return mapper.toDomain(repo.save(mapper.toEntity(contract)));
    }

    @Override
    public Optional<Contract> findById(ContractId id) {
        return repo.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<Contract> findByShareToken(String token) {
        return repo.findByShareToken(token).map(mapper::toDomain);
    }

    @Override
    public List<Contract> findByPromoterId(PromoterId promoterId) {
        return repo.findByPromoterId(promoterId.value()).stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    public List<Contract> findByPromoterIdAndStatus(PromoterId promoterId, ContractStatus status) {
        return repo.findByPromoterIdAndStatus(promoterId.value(), status).stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    public List<Contract> findByPromoterIdAndType(PromoterId promoterId, ContractType type) {
        return repo.findByPromoterIdAndType(promoterId.value(), type).stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    public List<Contract> findByLinkedEventId(java.util.UUID eventId) {
        return repo.findByLinkedEventId(eventId).stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    public ContractStats statsForPromoter(PromoterId promoterId) {
        List<Object[]> statusCounts = repo.countGroupedByStatus(promoterId.value());
        List<Object[]> typeCounts   = repo.countGroupedByType(promoterId.value());
        BigDecimal     totalFee     = repo.sumSignedFeeByPromoter(promoterId.value());

        long draft = 0, sent = 0, delivered = 0, signed = 0, archived = 0, cancelled = 0, voided = 0;
        for (Object[] row : statusCounts) {
            ContractStatus s = (ContractStatus) row[0];
            long count = (long) row[1];
            switch (s) {
                case DRAFT      -> draft     = count;
                case SENT       -> sent      = count;
                case DELIVERED  -> delivered = count;
                case SIGNED     -> signed    = count;
                case ARCHIVED   -> archived  = count;
                case CANCELLED  -> cancelled = count;
                case VOIDED     -> voided    = count;
            }
        }

        Map<ContractType, Long> byType = new EnumMap<>(ContractType.class);
        for (Object[] row : typeCounts) {
            byType.put((ContractType) row[0], (long) row[1]);
        }

        return new ContractStats(draft, sent, delivered, signed, archived, cancelled, voided,
                totalFee != null ? totalFee : BigDecimal.ZERO, byType);
    }

    @Override
    public int countByPromoterIdForYear(PromoterId promoterId, int year) {
        return repo.countByPromoterIdAndYearPrefix(promoterId.value(), "GRK-" + year);
    }
}
