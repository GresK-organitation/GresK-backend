package com.gresk.modules.ticket.domain.port.out;

import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.event.domain.model.Price;
import com.gresk.modules.ticket.domain.model.PaymentResult;
import com.gresk.modules.user.domain.model.UserId;

public interface PaymentGateway {

    PaymentResult processPayment(UserId userId, EventId eventId, Price amount);
}
