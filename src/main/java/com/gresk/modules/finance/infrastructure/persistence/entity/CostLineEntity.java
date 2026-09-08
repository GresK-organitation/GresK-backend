package com.gresk.modules.finance.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "cost_lines")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CostLineEntity {

    @Id
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "plan_id", nullable = false)
    private UUID planId;

    @Column(nullable = false, length = 20)
    private String category;

    @Column(nullable = false, length = 30)
    private String subcategory;

    @Column(length = 500)
    private String description;

    @Column(name = "budgeted_amount", precision = 12, scale = 2)
    private BigDecimal budgetedAmount;

    @Column(name = "variable_percentage", precision = 5, scale = 2)
    private BigDecimal variablePercentage;

    @Column(length = 3)
    private String currency;
}
