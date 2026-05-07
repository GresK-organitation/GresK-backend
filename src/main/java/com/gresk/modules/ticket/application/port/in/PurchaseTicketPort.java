package com.gresk.modules.ticket.application.port.in;

import com.gresk.modules.ticket.application.usecase.PurchaseTicketCommand;
import com.gresk.modules.ticket.domain.model.Ticket;

public interface PurchaseTicketPort {
    Ticket execute(PurchaseTicketCommand command);
}
