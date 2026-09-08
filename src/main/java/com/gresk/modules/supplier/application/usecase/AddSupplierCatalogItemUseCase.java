package com.gresk.modules.supplier.application.usecase;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.supplier.application.command.AddSupplierCatalogItemCommand;
import com.gresk.modules.supplier.domain.exception.SupplierNotFoundException;
import com.gresk.modules.supplier.domain.exception.SupplierNotOwnedException;
import com.gresk.modules.supplier.domain.model.CatalogItem;
import com.gresk.modules.supplier.domain.model.Supplier;
import com.gresk.modules.supplier.domain.model.SupplierId;
import com.gresk.modules.supplier.domain.model.valueobject.PricingUnit;
import com.gresk.modules.supplier.domain.model.valueobject.SupplierCategory;
import com.gresk.modules.supplier.domain.port.out.SupplierRepositoryPort;
import com.gresk.shared.domain.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddSupplierCatalogItemUseCase {

    private final SupplierRepositoryPort supplierRepository;

    @Transactional
    public CatalogItem execute(AddSupplierCatalogItemCommand command) {
        Supplier supplier = supplierRepository.findById(SupplierId.of(command.supplierId()))
                .orElseThrow(() -> new SupplierNotFoundException(command.supplierId()));

        if (!supplier.getPromoterId().equals(PromoterId.of(command.promoterId()))) {
            throw new SupplierNotOwnedException(command.supplierId());
        }

        Money unitPrice = Money.of(command.unitPriceAmount(), command.unitPriceCurrency());
        CatalogItem item = supplier.addCatalogItem(
                SupplierCategory.valueOf(command.category()), command.itemName(), unitPrice,
                PricingUnit.valueOf(command.pricingUnit()), command.leadTimeDays(), command.notes());

        supplierRepository.save(supplier);
        return item;
    }
}
