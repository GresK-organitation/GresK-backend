package com.gresk.modules.agenda.infrastructure.google;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "gresk.agenda.google-calendar")
public record GoogleCalendarProperties(
        @DefaultValue("") String clientId,
        @DefaultValue("") String clientSecret,
        @DefaultValue("") String redirectUri,
        /** URL pública que recibirá las notificaciones push del canal watch, p. ej. https://api.gresk.com/api/v1/agenda/calendar-sync/webhook/google */
        @DefaultValue("") String webhookUrl
) {
}
