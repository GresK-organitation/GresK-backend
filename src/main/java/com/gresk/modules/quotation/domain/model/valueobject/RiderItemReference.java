package com.gresk.modules.quotation.domain.model.valueobject;

import java.util.UUID;

public record RiderItemReference(RiderType riderType, UUID riderId, UUID lineItemId) {

    public RiderItemReference {
        if (riderType == null) throw new IllegalArgumentException("riderType cannot be null");
        if (riderId == null) throw new IllegalArgumentException("riderId cannot be null");
        if (lineItemId == null) throw new IllegalArgumentException("lineItemId cannot be null");
    }
}
