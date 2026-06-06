package com.gresk.modules.contract.domain.model.valueobject;

import java.math.BigDecimal;

public record PaymentTerm(
        BigDecimal percentage,
        String     description,
        String     method,
        boolean    paid
) {}
