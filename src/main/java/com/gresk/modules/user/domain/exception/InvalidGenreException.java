package com.gresk.modules.user.domain.exception;

public class InvalidGenreException extends RuntimeException {
    public InvalidGenreException(String genre) {
        super("Invalid musical genre: " + genre);
    }
}
