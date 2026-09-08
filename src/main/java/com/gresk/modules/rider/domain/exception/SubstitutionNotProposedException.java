package com.gresk.modules.rider.domain.exception;

import java.util.UUID;

public class SubstitutionNotProposedException extends RuntimeException {
    public SubstitutionNotProposedException(UUID lineItemId) {
        super("No substitution has been proposed for line item: " + lineItemId);
    }
}
