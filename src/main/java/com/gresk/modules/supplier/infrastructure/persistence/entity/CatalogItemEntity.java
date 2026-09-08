package com.gresk.modules.supplier.infrastructure.persistence.entity;

import com.gresk.modules.supplier.domain.model.valueobject.PricingUnit;
import com.gresk.modules.supplier.domain.model.valueobject.SupplierCategory;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "supplier_catalog_items")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CatalogItemEntity {

    @Id
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private SupplierEntity supplier;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 20, nullable = false)
    private SupplierCategory category;

    @Column(name = "item_name", length = 255, nullable = false)
    private String itemName;

    @Column(name = "unit_price_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal unitPriceAmount;

    @Column(name = "unit_price_currency", length = 3, nullable = false)
    private String unitPriceCurrency;

    @Enumerated(EnumType.STRING)
    @Column(name = "pricing_unit", length = 20, nullable = false)
    private PricingUnit pricingUnit;

    @Column(name = "lead_time_days")
    private Integer leadTimeDays;

    @Column(nullable = false)
    private boolean active;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
