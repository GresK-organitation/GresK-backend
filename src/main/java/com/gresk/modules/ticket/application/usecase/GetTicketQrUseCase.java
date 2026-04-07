package com.gresk.modules.ticket.application.usecase;

import com.gresk.modules.ticket.domain.exception.TicketNotFoundException;
import com.gresk.modules.ticket.domain.exception.UnauthorizedTicketAccessException;
import com.gresk.modules.ticket.domain.model.Ticket;
import com.gresk.modules.ticket.domain.model.TicketId;
import com.gresk.modules.ticket.domain.port.out.QrCodeGenerator;
import com.gresk.modules.ticket.domain.port.out.TicketRepository;
import com.gresk.modules.user.domain.model.UserId;

public class GetTicketQrUseCase {

    private final TicketRepository ticketRepository;
    private final QrCodeGenerator qrCodeGenerator;

    public GetTicketQrUseCase(TicketRepository ticketRepository, QrCodeGenerator qrCodeGenerator) {
        this.ticketRepository = ticketRepository;
        this.qrCodeGenerator = qrCodeGenerator;
    }

    public byte[] execute(GetTicketQrQuery query) {
        TicketId ticketId = TicketId.from(query.ticketId());
        UserId requestingUserId = UserId.from(query.userId());

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found: " + query.ticketId()));

        if (!ticket.getUserId().equals(requestingUserId)) {
            throw new UnauthorizedTicketAccessException(
                    "User " + query.userId() + " is not the owner of ticket " + query.ticketId());
        }

        return qrCodeGenerator.renderToImage(ticket.getQrCode());
    }
}
