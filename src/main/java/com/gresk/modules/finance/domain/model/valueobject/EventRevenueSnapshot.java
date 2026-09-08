package com.gresk.modules.finance.domain.model.valueobject;

import com.gresk.shared.domain.valueobject.Money;

import java.time.Instant;

/**
 * Resultado del puerto de lectura hacia event/ticket: taquilla bruta calculada en caliente
 * (tickets no cancelados x precio efectivo del evento) en el momento de la consulta.
 */
public record EventRevenueSnapshot(
        Money grossBoxOffice,
        int ticketsSold,
        Instant capturedAt
) {
    public EventRevenueSnapshot {
        if (grossBoxOffice == null) throw new IllegalArgumentException("grossBoxOffice is required");
        if (ticketsSold < 0) throw new IllegalArgumentException("ticketsSold cannot be negative");
    }
}
