package com.gresk.modules.agenda.domain.port.out;

import com.gresk.modules.agenda.domain.model.AgendaEntry;

public interface MailNotificationPort {
    void sendReminderEmail(AgendaEntry entry, String promoterEmail);
}
