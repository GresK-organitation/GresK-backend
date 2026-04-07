package com.gresk.modules.ticket.infrastructure;

import com.gresk.modules.ticket.application.usecase.PurchaseTicketCommand;
import com.gresk.modules.ticket.application.usecase.PurchaseTicketUseCase;
import com.gresk.modules.ticket.domain.model.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionalPurchaseTicketService {

    private final PurchaseTicketUseCase purchaseTicketUseCase;

    @Transactional(rollbackFor = Exception.class)
    public Ticket execute(PurchaseTicketCommand command) {
        return purchaseTicketUseCase.execute(command);
    }
}
