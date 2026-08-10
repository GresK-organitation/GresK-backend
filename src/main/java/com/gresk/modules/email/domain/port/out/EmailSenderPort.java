package com.gresk.modules.email.domain.port.out;

/** Envío de correos salientes (respuestas aprobadas por la promotora). */
public interface EmailSenderPort {
    void send(String to, String subject, String body);
}
