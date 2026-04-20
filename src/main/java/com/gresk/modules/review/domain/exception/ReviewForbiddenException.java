package com.gresk.modules.review.domain.exception;

public class ReviewForbiddenException extends RuntimeException {
    public ReviewForbiddenException(String message) {
        super(message);
    }
}
