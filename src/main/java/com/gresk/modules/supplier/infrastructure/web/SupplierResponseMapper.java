package com.gresk.modules.supplier.infrastructure.web;

import com.gresk.modules.supplier.domain.model.CatalogItem;
import com.gresk.modules.supplier.domain.model.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierResponseMapper {

    public SupplierResponse toResponse(Supplier s) {
        return new SupplierResponse(
                s.getId().toString(),
                s.getPromoterId().value().toString(),
                s.getName(),
                s.getSpecialties().stream().map(Enum::name).collect(java.util.stream.Collectors.toSet()),
                s.getContact() != null ? s.getContact().contactName() : null,
                s.getContact() != null ? s.getContact().email() : null,
                s.getContact() != null ? s.getContact().phone() : null,
                s.getServiceCity(),
                s.isActive(),
                s.getCatalog().stream().map(this::toResponse).toList(),
                s.getCreatedAt()
        );
    }

    public CatalogItemResponse toResponse(CatalogItem item) {
        return new CatalogItemResponse(
                item.getId(),
                item.getCategory().name(),
                item.getItemName(),
                item.getUnitPrice().amount(),
                item.getUnitPrice().currency(),
                item.getPricingUnit().name(),
                item.getLeadTimeDays(),
                item.isActive(),
                item.getNotes()
        );
    }
}
