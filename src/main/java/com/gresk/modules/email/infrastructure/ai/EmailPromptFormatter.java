package com.gresk.modules.email.infrastructure.ai;

import com.gresk.modules.email.domain.model.EmailMessage;

/** Representación compacta de un email para los prompts de clasificación. */
final class EmailPromptFormatter {

    private static final int MAX_BODY_CHARS = 4000;

    private EmailPromptFormatter() {}

    static String format(EmailMessage message) {
        return "De: " + nullSafe(message.getFromName()) + " <" + message.getFromAddress() + ">\n"
                + "Asunto: " + nullSafe(message.getSubject()) + "\n\n"
                + truncate(nullSafe(message.getBodyText()));
    }

    private static String truncate(String body) {
        return body.length() <= MAX_BODY_CHARS ? body : body.substring(0, MAX_BODY_CHARS) + "\n[...]";
    }

    private static String nullSafe(String value) {
        return value != null ? value : "";
    }
}
