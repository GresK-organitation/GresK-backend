package com.gresk.modules.agenda.infrastructure.outlook;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "gresk.agenda.outlook")
public record OutlookProperties(
        @DefaultValue("") String clientId,
        @DefaultValue("") String clientSecret,
        @DefaultValue("common") String tenantId,
        @DefaultValue("") String redirectUri,
        /** URL pública que recibirá las notificaciones de la suscripción de Microsoft Graph. */
        @DefaultValue("") String webhookUrl
) {
    String authorizeEndpoint() {
        return "https://login.microsoftonline.com/" + tenantId() + "/oauth2/v2.0/authorize";
    }

    String tokenEndpoint() {
        return "https://login.microsoftonline.com/" + tenantId() + "/oauth2/v2.0/token";
    }
}
