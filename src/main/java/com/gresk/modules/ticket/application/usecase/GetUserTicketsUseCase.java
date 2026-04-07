package com.gresk.modules.ticket.application.usecase;

import com.gresk.modules.ticket.domain.model.Ticket;
import com.gresk.modules.ticket.domain.port.out.TicketRepository;
import com.gresk.modules.user.domain.model.UserId;

import java.util.List;

public class GetUserTicketsUseCase {

    private final TicketRepository ticketRepository;

    public GetUserTicketsUseCase(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public List<Ticket> execute(GetUserTicketsQuery query) {
        return ticketRepository.findByUserId(UserId.from(query.userId()));
    }
}
