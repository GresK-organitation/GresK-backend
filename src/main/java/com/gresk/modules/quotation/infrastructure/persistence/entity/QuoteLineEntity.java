package com.gresk.modules.quotation.infrastructure.persistence.entity;

import com.gresk.modules.quotation.domain.model.valueobject.RiderType;
import com.gresk.modules.rider.domain.model.RiderItemCategory;
import com.gresk.modules.rider.domain.model.valueobject.FulfillmentSource;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "quote_lines")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QuoteLineEntity {

    @Id
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", nullable = false)
    private EventQuoteEntity quote;

    @Enumerated(EnumType.STRING)
    @Column(name = "rider_type", length = 20, nullable = false)
    private RiderType riderType;

    @Column(name = "rider_id", nullable = false)
    private UUID riderId;

    @Column(name = "line_item_id", nullable = false)
    private UUID lineItemId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 20, nullable = false)
    private RiderItemCategory category;

    @Column(name = "description", length = 255, nullable = false)
    private String description;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "fulfillment_source", length = 20, nullable = false)
    private FulfillmentSource fulfillmentSource;

    @Column(name = "supplier_id")
    private UUID supplierId;

    @Column(name = "catalog_item_id")
    private UUID catalogItemId;

    @Column(name = "unit_cost_amount", precision = 12, scale = 2)
    private BigDecimal unitCostAmount;

    @Column(name = "unit_cost_currency", length = 3)
    private String unitCostCurrency;
}
