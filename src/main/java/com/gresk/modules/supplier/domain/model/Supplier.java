package com.gresk.modules.supplier.domain.model;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.supplier.domain.model.valueobject.PricingUnit;
import com.gresk.modules.supplier.domain.model.valueobject.SupplierCategory;
import com.gresk.modules.supplier.domain.model.valueobject.SupplierContact;
import com.gresk.shared.domain.valueobject.Money;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class Supplier {

    private final SupplierId id;
    private final PromoterId promoterId;
    private final Instant createdAt;

    private String name;
    private Set<SupplierCategory> specialties;
    private SupplierContact contact;
    private String serviceCity;
    private boolean active;
    private final List<CatalogItem> catalog;

    private Supplier(SupplierId id, PromoterId promoterId, String name, Set<SupplierCategory> specialties,
                      SupplierContact contact, String serviceCity, boolean active, List<CatalogItem> catalog,
                      Instant createdAt) {
        this.id = id;
        this.promoterId = promoterId;
        this.name = name;
        this.specialties = specialties != null ? new HashSet<>(specialties) : new HashSet<>();
        this.contact = contact;
        this.serviceCity = serviceCity;
        this.active = active;
        this.catalog = catalog != null ? new ArrayList<>(catalog) : new ArrayList<>();
        this.createdAt = createdAt;
    }

    public static Supplier create(PromoterId promoterId, String name, Set<SupplierCategory> specialties,
                                   SupplierContact contact, String serviceCity) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Supplier name cannot be blank");
        return new Supplier(SupplierId.generate(), promoterId, name, specialties, contact, serviceCity,
                true, List.of(), Instant.now());
    }

    public static Supplier reconstitute(SupplierId id, PromoterId promoterId, String name,
                                         Set<SupplierCategory> specialties, SupplierContact contact,
                                         String serviceCity, boolean active, List<CatalogItem> catalog,
                                         Instant createdAt) {
        return new Supplier(id, promoterId, name, specialties, contact, serviceCity, active, catalog, createdAt);
    }

    public CatalogItem addCatalogItem(SupplierCategory category, String itemName, Money unitPrice,
                                       PricingUnit pricingUnit, Integer leadTimeDays, String notes) {
        CatalogItem item = CatalogItem.create(category, itemName, unitPrice, pricingUnit, leadTimeDays, notes);
        catalog.add(item);
        return item;
    }

    public CatalogItem catalogItem(UUID catalogItemId) {
        return catalog.stream().filter(c -> c.getId().equals(catalogItemId)).findFirst()
                .orElseThrow(() -> new java.util.NoSuchElementException("Catalog item not found: " + catalogItemId));
    }

    public void deactivate() {
        this.active = false;
    }

    public SupplierId getId() { return id; }
    public PromoterId getPromoterId() { return promoterId; }
    public String getName() { return name; }
    public Set<SupplierCategory> getSpecialties() { return Set.copyOf(specialties); }
    public SupplierContact getContact() { return contact; }
    public String getServiceCity() { return serviceCity; }
    public boolean isActive() { return active; }
    public List<CatalogItem> getCatalog() { return List.copyOf(catalog); }
    public Instant getCreatedAt() { return createdAt; }
}
