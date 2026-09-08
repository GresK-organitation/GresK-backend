package com.gresk.modules.quotation.domain.model;

import com.gresk.modules.quotation.domain.model.valueobject.RiderItemReference;
import com.gresk.modules.rider.domain.model.RiderItemCategory;
import com.gresk.modules.rider.domain.model.valueobject.FulfillmentSource;
import com.gresk.modules.supplier.domain.model.SupplierId;
import com.gresk.shared.domain.valueobject.Money;

import java.util.Optional;
import java.util.UUID;

public final class QuoteLine {

    private final UUID id;
    private final RiderItemReference riderItemRef;
    private final RiderItemCategory category;
    private final String description;
    private final int quantity;
    private FulfillmentSource fulfillmentSource;
    private SupplierId supplierId;
    private UUID catalogItemId;
    private Money unitCost;

    private QuoteLine(UUID id, RiderItemReference riderItemRef, RiderItemCategory category, String description,
                       int quantity, FulfillmentSource fulfillmentSource, SupplierId supplierId,
                       UUID catalogItemId, Money unitCost) {
        this.id = id;
        this.riderItemRef = riderItemRef;
        this.category = category;
        this.description = description;
        this.quantity = quantity;
        this.fulfillmentSource = fulfillmentSource;
        this.supplierId = supplierId;
        this.catalogItemId = catalogItemId;
        this.unitCost = unitCost;
    }

    public static QuoteLine fromRiderItem(RiderItemReference ref, RiderItemCategory category, String description,
                                           int quantity, FulfillmentSource fulfillmentSource) {
        return new QuoteLine(UUID.randomUUID(), ref, category, description, quantity, fulfillmentSource,
                null, null, null);
    }

    public static QuoteLine reconstitute(UUID id, RiderItemReference riderItemRef, RiderItemCategory category,
                                          String description, int quantity, FulfillmentSource fulfillmentSource,
                                          SupplierId supplierId, UUID catalogItemId, Money unitCost) {
        return new QuoteLine(id, riderItemRef, category, description, quantity, fulfillmentSource,
                supplierId, catalogItemId, unitCost);
    }

    public void assignSupplier(SupplierId supplierId, UUID catalogItemId, Money unitCost) {
        this.supplierId = supplierId;
        this.catalogItemId = catalogItemId;
        this.unitCost = unitCost;
    }

    public Money subtotal(String fallbackCurrency) {
        if (unitCost == null) return Money.zero(fallbackCurrency);
        return unitCost.multiply(quantity);
    }

    public UUID getId() { return id; }
    public RiderItemReference getRiderItemRef() { return riderItemRef; }
    public RiderItemCategory getCategory() { return category; }
    public String getDescription() { return description; }
    public int getQuantity() { return quantity; }
    public FulfillmentSource getFulfillmentSource() { return fulfillmentSource; }
    public Optional<SupplierId> getSupplierId() { return Optional.ofNullable(supplierId); }
    public Optional<UUID> getCatalogItemId() { return Optional.ofNullable(catalogItemId); }
    public Optional<Money> getUnitCost() { return Optional.ofNullable(unitCost); }
}
