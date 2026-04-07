package com.gresk.modules.ticket.infrastructure.persistence;

import com.gresk.modules.ticket.domain.model.TicketStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JpaTicketEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "qr_code", nullable = false, unique = true, length = 512)
    private String qrCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketStatus status;

    @Column(name = "purchased_at", nullable = false)
    private ZonedDateTime purchasedAt;
}
