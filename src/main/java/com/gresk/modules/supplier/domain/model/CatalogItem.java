package com.gresk.modules.supplier.domain.model;

import com.gresk.modules.supplier.domain.model.valueobject.PricingUnit;
import com.gresk.modules.supplier.domain.model.valueobject.SupplierCategory;
import com.gresk.shared.domain.valueobject.Money;

import java.util.UUID;

public final class CatalogItem {

    private final UUID id;
    private final SupplierCategory category;
    private final String itemName;
    private Money unitPrice;
    private final PricingUnit pricingUnit;
    private final Integer leadTimeDays;
    private boolean active;
    private final String notes;

    private CatalogItem(UUID id, SupplierCategory category, String itemName, Money unitPrice,
                         PricingUnit pricingUnit, Integer leadTimeDays, boolean active, String notes) {
        this.id = id;
        this.category = category;
        this.itemName = itemName;
        this.unitPrice = unitPrice;
        this.pricingUnit = pricingUnit;
        this.leadTimeDays = leadTimeDays;
        this.active = active;
        this.notes = notes;
    }

    public static CatalogItem create(SupplierCategory category, String itemName, Money unitPrice,
                                      PricingUnit pricingUnit, Integer leadTimeDays, String notes) {
        if (category == null) throw new IllegalArgumentException("CatalogItem category cannot be null");
        if (itemName == null || itemName.isBlank()) throw new IllegalArgumentException("CatalogItem itemName cannot be blank");
        if (unitPrice == null) throw new IllegalArgumentException("CatalogItem unitPrice cannot be null");
        if (pricingUnit == null) throw new IllegalArgumentException("CatalogItem pricingUnit cannot be null");
        return new CatalogItem(UUID.randomUUID(), category, itemName, unitPrice, pricingUnit, leadTimeDays, true, notes);
    }

    public static CatalogItem reconstitute(UUID id, SupplierCategory category, String itemName, Money unitPrice,
                                            PricingUnit pricingUnit, Integer leadTimeDays, boolean active, String notes) {
        return new CatalogItem(id, category, itemName, unitPrice, pricingUnit, leadTimeDays, active, notes);
    }

    public void deactivate() {
        this.active = false;
    }

    public void updatePrice(Money newPrice) {
        if (newPrice == null) throw new IllegalArgumentException("newPrice cannot be null");
        this.unitPrice = newPrice;
    }

    public UUID getId() { return id; }
    public SupplierCategory getCategory() { return category; }
    public String getItemName() { return itemName; }
    public Money getUnitPrice() { return unitPrice; }
    public PricingUnit getPricingUnit() { return pricingUnit; }
    public Integer getLeadTimeDays() { return leadTimeDays; }
    public boolean isActive() { return active; }
    public String getNotes() { return notes; }
}
