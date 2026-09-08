package com.gresk.modules.show.application.port.in;

/** Invocado por un scheduler periódico, igual que {@code booking.ExpireHoldsUseCase}. */
public interface ExpireHoldsUseCase {
    int execute();
}
