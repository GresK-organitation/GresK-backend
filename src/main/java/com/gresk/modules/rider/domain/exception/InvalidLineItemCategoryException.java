package com.gresk.modules.rider.domain.exception;

import com.gresk.modules.rider.domain.model.RiderItemCategory;

public class InvalidLineItemCategoryException extends RuntimeException {
    public InvalidLineItemCategoryException(RiderItemCategory category) {
        super("Category not valid for this rider type: " + category);
    }
}
