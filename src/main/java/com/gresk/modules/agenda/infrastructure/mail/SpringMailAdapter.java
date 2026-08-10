package com.gresk.modules.agenda.infrastructure.mail;

import com.gresk.modules.agenda.domain.model.AgendaEntry;
import com.gresk.modules.agenda.domain.port.out.MailNotificationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpringMailAdapter implements MailNotificationPort {

    private final JavaMailSender mailSender;

    @Override
    public void sendReminderEmail(AgendaEntry entry, String promoterEmail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(promoterEmail);
            message.setSubject("⏰ Recordatorio: " + entry.getTitle());
            message.setText(buildBody(entry));
            mailSender.send(message);
            log.info("Reminder email sent for entry {} to {}", entry.getId(), promoterEmail);
        } catch (Exception ex) {
            log.error("Failed to send reminder email for entry {}: {}", entry.getId(), ex.getMessage());
        }
    }

    private String buildBody(AgendaEntry entry) {
        StringBuilder sb = new StringBuilder();
        sb.append("Tienes un recordatorio próximo:\n\n");
        sb.append("📌 ").append(entry.getTitle()).append("\n");
        sb.append("Tipo: ").append(entry.getType().name()).append("\n");
        if (entry.getStartAt() != null) {
            String formatted = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                    .withZone(ZoneOffset.UTC)
                    .format(entry.getStartAt());
            sb.append("Fecha: ").append(formatted).append(" UTC\n");
        }
        if (entry.getDescription() != null && !entry.getDescription().isBlank()) {
            sb.append("Descripción: ").append(entry.getDescription()).append("\n");
        }
        if (entry.getLinkedEntity() != null) {
            sb.append("Vinculado a: ").append(entry.getLinkedEntity().type().name())
              .append(" (").append(entry.getLinkedEntity().entityId()).append(")\n");
        }
        sb.append("\n— GresK");
        return sb.toString();
    }
}
