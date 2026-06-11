package com.gresk.modules.email.infrastructure.gmail;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "gresk.email.gmail")
public record GmailProperties(
        @DefaultValue("") String clientId,
        @DefaultValue("") String clientSecret,
        @DefaultValue("") String redirectUri,
        /** Topic Pub/Sub para gmail watch(), p. ej. projects/gresk/topics/gmail-inbox */
        @DefaultValue("") String pubsubTopic,
        /** Token compartido que Google añade como query param al push del webhook */
        @DefaultValue("") String pubsubVerificationToken
) {}
