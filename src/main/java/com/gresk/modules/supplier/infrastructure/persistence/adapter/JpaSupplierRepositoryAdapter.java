package com.gresk.modules.supplier.infrastructure.persistence.adapter;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.supplier.domain.model.Supplier;
import com.gresk.modules.supplier.domain.model.SupplierId;
import com.gresk.modules.supplier.domain.model.valueobject.SupplierCategory;
import com.gresk.modules.supplier.domain.port.out.SupplierRepositoryPort;
import com.gresk.modules.supplier.infrastructure.persistence.mapper.SupplierMapper;
import com.gresk.modules.supplier.infrastructure.persistence.repository.SupplierJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaSupplierRepositoryAdapter implements SupplierRepositoryPort {

    private final SupplierJpaRepository repo;
    private final SupplierMapper        mapper;

    @Override
    @Transactional
    public Supplier save(Supplier supplier) {
        return mapper.toDomain(repo.save(mapper.toEntity(supplier)));
    }

    @Override
    public Optional<Supplier> findById(SupplierId id) {
        return repo.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<Supplier> findByPromoterId(PromoterId promoterId) {
        return repo.findByPromoterId(promoterId.value()).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Supplier> findByPromoterAndCategory(PromoterId promoterId, SupplierCategory category, String city) {
        return repo.findByPromoterAndCategory(promoterId.value(), category, city).stream()
                .map(mapper::toDomain).toList();
    }
}
