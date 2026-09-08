package com.gresk.modules.supplier.domain.port.out;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.supplier.domain.model.Supplier;
import com.gresk.modules.supplier.domain.model.SupplierId;
import com.gresk.modules.supplier.domain.model.valueobject.SupplierCategory;

import java.util.List;
import java.util.Optional;

public interface SupplierRepositoryPort {

    Supplier save(Supplier supplier);

    Optional<Supplier> findById(SupplierId id);

    List<Supplier> findByPromoterId(PromoterId promoterId);

    List<Supplier> findByPromoterAndCategory(PromoterId promoterId, SupplierCategory category, String city);
}
