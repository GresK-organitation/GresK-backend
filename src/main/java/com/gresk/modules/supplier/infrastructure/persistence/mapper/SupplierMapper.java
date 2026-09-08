package com.gresk.modules.supplier.infrastructure.persistence.mapper;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.supplier.domain.model.CatalogItem;
import com.gresk.modules.supplier.domain.model.Supplier;
import com.gresk.modules.supplier.domain.model.SupplierId;
import com.gresk.modules.supplier.domain.model.valueobject.SupplierContact;
import com.gresk.modules.supplier.infrastructure.persistence.entity.CatalogItemEntity;
import com.gresk.modules.supplier.infrastructure.persistence.entity.SupplierEntity;
import com.gresk.shared.domain.valueobject.Money;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SupplierMapper {

    public Supplier toDomain(SupplierEntity e) {
        SupplierContact contact = e.getContactName() == null ? null :
                new SupplierContact(e.getContactName(), e.getContactEmail(), e.getContactPhone());

        List<CatalogItem> catalog = e.getCatalog().stream().map(this::toDomainCatalogItem).toList();

        return Supplier.reconstitute(
                SupplierId.of(e.getId()), PromoterId.of(e.getPromoterId()), e.getName(),
                e.getSpecialties(), contact, e.getServiceCity(), e.isActive(), catalog, e.getCreatedAt());
    }

    public SupplierEntity toEntity(Supplier s) {
        SupplierEntity entity = SupplierEntity.builder()
                .id(s.getId().value())
                .promoterId(s.getPromoterId().value())
                .name(s.getName())
                .contactName(s.getContact() != null ? s.getContact().contactName() : null)
                .contactEmail(s.getContact() != null ? s.getContact().email() : null)
                .contactPhone(s.getContact() != null ? s.getContact().phone() : null)
                .serviceCity(s.getServiceCity())
                .active(s.isActive())
                .specialties(s.getSpecialties())
                .createdAt(s.getCreatedAt())
                .build();

        List<CatalogItemEntity> catalog = new ArrayList<>();
        for (CatalogItem item : s.getCatalog()) {
            catalog.add(toEntityCatalogItem(item, entity));
        }
        entity.setCatalog(catalog);

        return entity;
    }

    private CatalogItem toDomainCatalogItem(CatalogItemEntity e) {
        return CatalogItem.reconstitute(e.getId(), e.getCategory(), e.getItemName(),
                Money.of(e.getUnitPriceAmount(), e.getUnitPriceCurrency()), e.getPricingUnit(),
                e.getLeadTimeDays(), e.isActive(), e.getNotes());
    }

    private CatalogItemEntity toEntityCatalogItem(CatalogItem item, SupplierEntity parent) {
        return CatalogItemEntity.builder()
                .id(item.getId())
                .supplier(parent)
                .category(item.getCategory())
                .itemName(item.getItemName())
                .unitPriceAmount(item.getUnitPrice().amount())
                .unitPriceCurrency(item.getUnitPrice().currency())
                .pricingUnit(item.getPricingUnit())
                .leadTimeDays(item.getLeadTimeDays())
                .active(item.isActive())
                .notes(item.getNotes())
                .build();
    }
}
