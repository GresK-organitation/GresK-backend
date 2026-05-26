package com.gresk.modules.review.domain.exception;

public class ReviewAlreadyLikedException extends RuntimeException {
    public ReviewAlreadyLikedException(String message) {
        super(message);
    }
}
