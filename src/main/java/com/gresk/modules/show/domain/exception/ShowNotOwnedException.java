package com.gresk.modules.show.domain.exception;

public class ShowNotOwnedException extends RuntimeException {
    public ShowNotOwnedException() {
        super("You do not have permission to access this show");
    }
}
