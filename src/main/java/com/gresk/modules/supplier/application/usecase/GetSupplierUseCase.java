package com.gresk.modules.supplier.application.usecase;

import com.gresk.modules.supplier.domain.exception.SupplierNotFoundException;
import com.gresk.modules.supplier.domain.model.Supplier;
import com.gresk.modules.supplier.domain.model.SupplierId;
import com.gresk.modules.supplier.domain.port.out.SupplierRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetSupplierUseCase {

    private final SupplierRepositoryPort supplierRepository;

    @Transactional(readOnly = true)
    public Supplier execute(String supplierId) {
        return supplierRepository.findById(SupplierId.of(supplierId))
                .orElseThrow(() -> new SupplierNotFoundException(supplierId));
    }
}
