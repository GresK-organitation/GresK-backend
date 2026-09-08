package com.gresk.modules.supplier.application.usecase;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.supplier.domain.exception.SupplierNotFoundException;
import com.gresk.modules.supplier.domain.exception.SupplierNotOwnedException;
import com.gresk.modules.supplier.domain.model.Supplier;
import com.gresk.modules.supplier.domain.model.SupplierId;
import com.gresk.modules.supplier.domain.port.out.SupplierRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeactivateSupplierUseCase {

    private final SupplierRepositoryPort supplierRepository;

    @Transactional
    public Supplier execute(String supplierId, String promoterId) {
        Supplier supplier = supplierRepository.findById(SupplierId.of(supplierId))
                .orElseThrow(() -> new SupplierNotFoundException(supplierId));

        if (!supplier.getPromoterId().equals(PromoterId.of(promoterId))) {
            throw new SupplierNotOwnedException(supplierId);
        }

        supplier.deactivate();
        return supplierRepository.save(supplier);
    }
}
