package com.gresk.modules.ticket.infrastructure;


import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.event.domain.model.Price;

import com.gresk.modules.ticket.domain.model.PaymentResult;
import com.gresk.modules.ticket.domain.port.out.PaymentGateway;
import com.gresk.modules.user.domain.model.UserId;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MockPaymentAdapter implements PaymentGateway {

    @Override
    public PaymentResult processPayment(UserId userId, EventId eventId, Price amount) {
        // Simulación: El pago siempre es exitoso a menos que el precio sea 0 o negativo
        if (amount.amount().doubleValue() <= 0) {
            return new PaymentResult(false, null);
        }

        return new PaymentResult(
                true,
                "mock_tx_" + UUID.randomUUID().toString().substring(0, 8)
        );
    }
}