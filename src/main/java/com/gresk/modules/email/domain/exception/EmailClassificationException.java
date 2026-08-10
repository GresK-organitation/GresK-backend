package com.gresk.modules.email.domain.exception;

/** Ninguna capa del pipeline pudo clasificar el correo (incluida la capa Claude). */
public class EmailClassificationException extends RuntimeException {
    public EmailClassificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
