package com.gresk.modules.user.domain.exception;

import com.gresk.modules.user.domain.model.UserId;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(UserId id) {
        super("User not found: " + id.value());
    }
}
