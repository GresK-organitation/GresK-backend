package com.gresk.modules.ticket.infrastructure.persistence;

import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.ticket.domain.model.*;
import com.gresk.modules.user.domain.model.UserId;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    public Ticket toDomain(JpaTicketEntity entity) {
        return Ticket.reconstitute(
                new TicketId(entity.getId()),
                UserId.of(entity.getUserId()),
                new EventId(entity.getEventId()),
                QrCode.of(entity.getQrCode()),
                entity.getStatus(),
                entity.getPurchasedAt()
        );
    }

    public JpaTicketEntity toEntity(Ticket ticket) {
        return JpaTicketEntity.builder()
                .id(ticket.getId().value())
                .userId(ticket.getUserId().value())
                .eventId(ticket.getEventId().value())
                .qrCode(ticket.getQrCode().value())
                .status(ticket.getStatus())
                .purchasedAt(ticket.getPurchasedAt())
                .build();
    }
}
