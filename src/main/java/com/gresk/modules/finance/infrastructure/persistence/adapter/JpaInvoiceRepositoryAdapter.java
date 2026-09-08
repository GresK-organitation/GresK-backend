package com.gresk.modules.finance.infrastructure.persistence.adapter;

import com.gresk.modules.finance.domain.model.Invoice;
import com.gresk.modules.finance.domain.model.InvoiceId;
import com.gresk.modules.finance.domain.port.out.InvoiceRepositoryPort;
import com.gresk.modules.finance.infrastructure.persistence.mapper.InvoiceMapper;
import com.gresk.modules.finance.infrastructure.persistence.repository.InvoiceJpaRepository;
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
public class JpaInvoiceRepositoryAdapter implements InvoiceRepositoryPort {

    private final InvoiceJpaRepository repo;
    private final InvoiceMapper        mapper;

    @Override
    @Transactional
    public Invoice save(Invoice invoice) {
        return mapper.toDomain(repo.save(mapper.toEntity(invoice)));
    }

    @Override
    public Optional<Invoice> findById(InvoiceId id) {
        return repo.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<Invoice> findByLinkedEventId(UUID linkedEventId) {
        return repo.findByLinkedEventId(linkedEventId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Invoice> findByPromoterId(PromoterId promoterId) {
        return repo.findByPromoterId(promoterId.value()).stream().map(mapper::toDomain).toList();
    }

    @Override
    public int countByPromoterIdForYear(PromoterId promoterId, int year) {
        return repo.countByPromoterIdAndYearPrefix(promoterId.value(), "INV-" + year);
    }
}
