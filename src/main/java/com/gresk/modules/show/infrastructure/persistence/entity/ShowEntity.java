package com.gresk.modules.show.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "shows")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowEntity {

    @Id
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "promoter_id", nullable = false)
    private UUID promoterId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "scheduled_date") private Instant scheduledDate;

    @Column(nullable = false, length = 20)
    private String status;

    // ── Venue booking snapshot ───────────────────────────────────────────────
    @Column(name = "venue_id")               private UUID   venueId;
    @Column(name = "venue_name", length = 255) private String venueName;
    @Column(name = "capacity_config_code", length = 50)   private String capacityConfigCode;
    @Column(name = "capacity_config_label", length = 255) private String capacityConfigLabel;
    @Column(name = "confirmed_capacity")     private Integer confirmedCapacity;

    // ── Hold window ───────────────────────────────────────────────────────────
    @Column(name = "hold_placed_at")  private Instant holdPlacedAt;
    @Column(name = "hold_expires_at") private Instant holdExpiresAt;

    // ── Financial simulation (P&L borrador) ──────────────────────────────────
    @Column(name = "cost_line_items", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String costLineItemsJson;
    @Column(name = "avg_ticket_price", precision = 10, scale = 2) private BigDecimal avgTicketPrice;
    @Column(name = "expected_sellout_percent")                    private Integer    expectedSelloutPercent;
    @Column(name = "simulation_currency", length = 10)            private String     simulationCurrency;

    // ── Settlement ────────────────────────────────────────────────────────────
    @Column(name = "settlement_actual_attendance")               private Integer    settlementActualAttendance;
    @Column(name = "settlement_actual_revenue", precision = 12, scale = 2) private BigDecimal settlementActualRevenue;
    @Column(name = "settlement_actual_costs",   precision = 12, scale = 2) private BigDecimal settlementActualCosts;
    @Column(name = "settlement_net_result",     precision = 12, scale = 2) private BigDecimal settlementNetResult;
    @Column(name = "settlement_settled_at")                      private Instant    settlementSettledAt;

    @Column(name = "linked_marketplace_event_id") private UUID linkedMarketplaceEventId;
    @Column(name = "cancellation_reason", columnDefinition = "TEXT") private String cancellationReason;

    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false)                    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        if (createdAt == null) createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
