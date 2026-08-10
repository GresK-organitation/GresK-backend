package com.gresk.modules.email.infrastructure.mail;

import com.gresk.modules.email.domain.port.out.EmailSenderPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Envío vía SMTP (configuración spring.mail existente). Las excepciones se
 * propagan a propósito: si el envío falla, la aprobación del borrador debe
 * revertir. En el Issue #3 podrá sustituirse por la API de Gmail.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SmtpEmailSenderAdapter implements EmailSenderPort {

    private final JavaMailSender mailSender;

    @Override
    public void send(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
        log.info("Reply sent to {}", to);
    }
}
