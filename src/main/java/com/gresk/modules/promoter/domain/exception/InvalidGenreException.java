package com.gresk.modules.promoter.domain.exception;

public class InvalidGenreException extends RuntimeException {
    public InvalidGenreException(String genre) {
        super("Invalid musical genre: " + genre);
    }
}
