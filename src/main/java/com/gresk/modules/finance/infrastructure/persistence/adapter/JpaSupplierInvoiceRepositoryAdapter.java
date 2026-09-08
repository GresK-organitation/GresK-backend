package com.gresk.modules.finance.infrastructure.persistence.adapter;

import com.gresk.modules.finance.domain.model.SupplierInvoice;
import com.gresk.modules.finance.domain.model.SupplierInvoiceId;
import com.gresk.modules.finance.domain.model.valueobject.CostLineId;
import com.gresk.modules.finance.domain.port.out.SupplierInvoiceRepositoryPort;
import com.gresk.modules.finance.infrastructure.persistence.mapper.SupplierInvoiceMapper;
import com.gresk.modules.finance.infrastructure.persistence.repository.SupplierInvoiceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaSupplierInvoiceRepositoryAdapter implements SupplierInvoiceRepositoryPort {

    private final SupplierInvoiceJpaRepository repo;
    private final SupplierInvoiceMapper        mapper;

    @Override
    @Transactional
    public SupplierInvoice save(SupplierInvoice invoice) {
        return mapper.toDomain(repo.save(mapper.toEntity(invoice)));
    }

    @Override
    public Optional<SupplierInvoice> findById(SupplierInvoiceId id) {
        return repo.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<SupplierInvoice> findByLinkedEventId(UUID linkedEventId) {
        return repo.findByLinkedEventId(linkedEventId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<SupplierInvoice> findByLinkedCostLineId(CostLineId costLineId) {
        return repo.findByLinkedCostLineId(costLineId.value()).stream().map(mapper::toDomain).toList();
    }
}
