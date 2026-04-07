package com.gresk.modules.ticket.domain.port.out;

import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.ticket.domain.model.Ticket;
import com.gresk.modules.ticket.domain.model.TicketId;
import com.gresk.modules.user.domain.model.UserId;

import java.util.List;
import java.util.Optional;

public interface TicketRepository {

    Ticket save(Ticket ticket);

    Optional<Ticket> findById(TicketId id);

    List<Ticket> findByUserId(UserId userId);

    boolean existsByUserIdAndEventId(UserId userId, EventId eventId);
}
