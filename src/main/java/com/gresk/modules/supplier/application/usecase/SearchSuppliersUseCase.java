package com.gresk.modules.supplier.application.usecase;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.supplier.domain.model.Supplier;
import com.gresk.modules.supplier.domain.model.valueobject.SupplierCategory;
import com.gresk.modules.supplier.domain.port.out.SupplierRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchSuppliersUseCase {

    private final SupplierRepositoryPort supplierRepository;

    @Transactional(readOnly = true)
    public List<Supplier> execute(String promoterId, String category, String city) {
        PromoterId pid = PromoterId.of(promoterId);
        if (category == null) {
            return supplierRepository.findByPromoterId(pid);
        }
        return supplierRepository.findByPromoterAndCategory(pid, SupplierCategory.valueOf(category), city);
    }
}
