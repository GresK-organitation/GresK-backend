package com.gresk.modules.ticket.infrastructure.payment;

import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.event.domain.model.Price;
import com.gresk.modules.ticket.domain.model.PaymentResult;
import com.gresk.modules.ticket.domain.port.out.PaymentGateway;
import com.gresk.modules.user.domain.model.UserId;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Profile({"dev", "test"})
public class StubPaymentGateway implements PaymentGateway {

    private static final Logger log = LoggerFactory.getLogger(StubPaymentGateway.class);

    @PostConstruct
    public void init() {
        log.warn("StubPaymentGateway active — payments are simulated");
    }

    @Override
    public PaymentResult processPayment(UserId userId, EventId eventId, Price amount) {
        return new PaymentResult(true, UUID.randomUUID().toString());
    }
}
