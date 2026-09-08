package com.gresk.modules.booking.application.port.in;

public interface ExpireHoldsUseCase {
    /** @return número de holds expirados en esta ejecución */
    int execute();
}
