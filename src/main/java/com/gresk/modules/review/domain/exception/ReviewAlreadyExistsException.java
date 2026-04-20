package com.gresk.modules.review.domain.exception;

public class ReviewAlreadyExistsException extends RuntimeException {
    public ReviewAlreadyExistsException(String message) {
        super(message);
    }
}
