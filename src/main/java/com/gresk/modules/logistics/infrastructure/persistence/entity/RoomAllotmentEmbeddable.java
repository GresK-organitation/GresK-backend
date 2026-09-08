package com.gresk.modules.logistics.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomAllotmentEmbeddable {

    @Column(name = "room_type", nullable = false)
    private String roomType;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "cost_amount", nullable = false)
    private BigDecimal costAmount;

    @Column(name = "cost_currency", nullable = false, length = 3)
    private String currency;
}
